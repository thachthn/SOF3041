package com.fpt.testingasm.service;

import com.fpt.testingasm.model.User;
import com.fpt.testingasm.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit/Integration test cho UserService.
 * Tương ứng test case: TC-REG-01, 02, 03, 08-12, TC-LOG-01, 02, 03 (file 02_TestCases.md)
 */
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // TC-REG-01: Đăng ký hợp lệ
    @Test
    @Tag("smoke")
    @DisplayName("TC-REG-01: Đăng ký với dữ liệu hợp lệ phải thành công")
    void register_withValidData_shouldSucceed() {
        User user = userService.register("hungnt01", "hung01@gmail.com", "123456");

        assertNotNull(user.getId());
        assertEquals("hungnt01", user.getUsername());
        assertTrue(userRepository.existsByUsername("hungnt01"));
    }

    // TC-REG-02: Username đã tồn tại
    @Test
    @DisplayName("TC-REG-02: Đăng ký với username đã tồn tại phải báo lỗi")
    void register_withDuplicateUsername_shouldThrow() {
        userService.register("duplicateuser", "a@gmail.com", "123456");

        UserService.RegistrationException ex = assertThrows(
                UserService.RegistrationException.class,
                () -> userService.register("duplicateuser", "b@gmail.com", "123456"));

        assertTrue(ex.getMessage().contains("Username"));
    }

    // TC-REG-03: Email đã tồn tại
    @Test
    @DisplayName("TC-REG-03: Đăng ký với email đã tồn tại phải báo lỗi")
    void register_withDuplicateEmail_shouldThrow() {
        userService.register("userone", "dup@gmail.com", "123456");

        UserService.RegistrationException ex = assertThrows(
                UserService.RegistrationException.class,
                () -> userService.register("usertwo", "dup@gmail.com", "123456"));

        assertTrue(ex.getMessage().contains("Email"));
    }

    // TC-REG-04 / TC-REG-05: Boundary username 4 ký tự (hợp lệ) / 3 ký tự (không hợp lệ)
    @Test
    @DisplayName("TC-REG-04: Username đúng 4 ký tự (biên dưới) phải đăng ký được")
    void register_withUsername4Chars_shouldSucceed() {
        User user = userService.register("abcd", "abcd@gmail.com", "123456");
        assertNotNull(user.getId());
    }

    @Test
    @DisplayName("TC-REG-05: Username 3 ký tự (dưới biên) phải vi phạm validation")
    void register_withUsername3Chars_shouldViolateValidation() {
        User invalid = new User("abc", "abc3@gmail.com", "hashedpw123");
        assertThrows(ConstraintViolationException.class,
                () -> userRepository.saveAndFlush(invalid));
    }

    // TC-REG-08: Username rỗng
    @Test
    @DisplayName("TC-REG-08: Username rỗng phải vi phạm validation")
    void register_withBlankUsername_shouldViolateValidation() {
        User invalid = new User("", "blank@gmail.com", "hashedpw123");
        assertThrows(ConstraintViolationException.class,
                () -> userRepository.saveAndFlush(invalid));
    }

    // TC-REG-09: Email sai định dạng
    @Test
    @DisplayName("TC-REG-09: Email sai định dạng phải vi phạm validation")
    void register_withInvalidEmailFormat_shouldViolateValidation() {
        User invalid = new User("validuser1", "khong-hop-le", "hashedpw123");
        assertThrows(ConstraintViolationException.class,
                () -> userRepository.saveAndFlush(invalid));
    }

    // TC-REG-12: Mật khẩu phải được mã hoá, không lưu plain-text
    @Test
    @DisplayName("TC-REG-12: Mật khẩu lưu trong DB phải là hash, không phải plain-text")
    void register_passwordMustBeHashed() {
        User user = userService.register("securepassuser", "secure@gmail.com", "plaintext1");
        assertNotEquals("plaintext1", user.getPasswordHash());
        assertTrue(user.getPasswordHash().startsWith("$2")); // BCrypt prefix
    }

    // TC-LOG-01: Đăng nhập đúng username + đúng password
    @Test
    @Tag("smoke")
    @DisplayName("TC-LOG-01: Đăng nhập đúng tài khoản phải trả về user")
    void login_withCorrectCredentials_shouldReturnUser() {
        userService.register("loginok", "loginok@gmail.com", "mypassword");

        Optional<User> result = userService.login("loginok", "mypassword");

        assertTrue(result.isPresent());
        assertEquals("loginok", result.get().getUsername());
    }

    // TC-LOG-02: Sai password
    @Test
    @Tag("smoke")
    @DisplayName("TC-LOG-02: Đăng nhập sai password phải trả về rỗng")
    void login_withWrongPassword_shouldReturnEmpty() {
        userService.register("loginwrongpw", "wp@gmail.com", "correctpass");

        Optional<User> result = userService.login("loginwrongpw", "wrongpass");

        assertTrue(result.isEmpty());
    }

    // TC-LOG-03: Username không tồn tại
    @Test
    @DisplayName("TC-LOG-03: Đăng nhập với username không tồn tại phải trả về rỗng")
    void login_withNonExistentUsername_shouldReturnEmpty() {
        Optional<User> result = userService.login("khongtontai999", "anypassword");
        assertTrue(result.isEmpty());
    }
}
