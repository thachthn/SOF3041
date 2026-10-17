package com.fpt.testingasm.service;

import com.fpt.testingasm.model.User;
import com.fpt.testingasm.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Business rules (dùng làm "RAG context" cho Y2 - sinh test case bằng Gen AI):
 *
 * BR1. Username phải duy nhất trong hệ thống (không được trùng).
 * BR2. Email phải duy nhất trong hệ thống (không được trùng).
 * BR3. Username bắt buộc, độ dài 4-30 ký tự.
 * BR4. Email bắt buộc, đúng định dạng email.
 * BR5. Mật khẩu bắt buộc, tối thiểu 6 ký tự, được mã hoá (BCrypt) trước khi lưu DB,
 *      không bao giờ lưu plain-text.
 * BR6. Đăng nhập thất bại nếu username không tồn tại HOẶC mật khẩu không khớp;
 *      thông báo lỗi chung chung ("Sai tài khoản hoặc mật khẩu") để tránh lộ
 *      thông tin username nào tồn tại (security best practice).
 * BR7. Đăng ký thất bại nếu username hoặc email đã tồn tại -> trả lỗi tương ứng.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static class RegistrationException extends RuntimeException {
        public RegistrationException(String message) {
            super(message);
        }
    }

    /** BR1, BR2, BR7: kiểm tra trùng username/email trước khi tạo tài khoản. */
    public User register(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new RegistrationException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("Email đã được sử dụng");
        }
        String hash = passwordEncoder.encode(rawPassword);
        User user = new User(username, email, hash);
        return userRepository.save(user);
    }

    /** BR6: trả Optional rỗng nếu đăng nhập thất bại, không phân biệt lý do cụ thể ra ngoài. */
    public Optional<User> login(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPasswordHash()));
    }
}
