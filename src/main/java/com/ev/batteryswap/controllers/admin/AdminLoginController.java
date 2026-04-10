package com.ev.batteryswap.controllers.admin;

import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminLoginController(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public String processLogin(
        @RequestParam String username,
        @RequestParam String password,
        HttpSession session,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        User user = userRepository.findByUsername(username);

        if (
            user == null ||
            !passwordEncoder.matches(password, user.getPassword())
        ) {
            model.addAttribute("error", "Tài khoản hoặc mật khẩu không đúng!");
            return "admin/login";
        }

        if (!"ADMIN".equals(user.getRole())) {
            model.addAttribute("accessDenied", true);
            return "admin/login";
        }

        session.setAttribute("adminUser", user.getUsername());
        session.setAttribute("adminFullName", user.getFullName());
        redirectAttributes.addFlashAttribute(
            "loginSuccess",
            "Đăng nhập thành công! Chào mừng, " + user.getFullName() + "!"
        );
        return "redirect:/admin/dashboard";
    }
}
