package com.fpt.testingasm.controller;

import com.fpt.testingasm.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                            @RequestParam String email,
                            @RequestParam String password,
                            Model model) {
        try {
            userService.register(username, email, password);
            model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "login";
        } catch (UserService.RegistrationException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "register";
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                         @RequestParam String password,
                         HttpSession session,
                         Model model) {
        return userService.login(username, password)
                .map(user -> {
                    session.setAttribute("currentUser", user.getUsername());
                    return "redirect:/products";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
                    model.addAttribute("username", username);
                    return "login";
                });
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        return session.getAttribute("currentUser") != null ? "redirect:/products" : "redirect:/login";
    }
}
