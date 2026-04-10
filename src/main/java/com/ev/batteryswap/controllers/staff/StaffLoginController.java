package com.ev.batteryswap.controllers.staff;

import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff")
public class StaffLoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffLoginController(
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
            return "staff/login";
        }

        if (!"STAFF".equals(user.getRole())) {
            model.addAttribute("accessDenied", true);
            return "staff/login";
        }

        session.setAttribute("staffUser", user.getUsername());
        session.setAttribute("staffFullName", user.getFullName());
        redirectAttributes.addFlashAttribute(
            "loginSuccess",
            "Đăng nhập thành công! Chào mừng, " + user.getFullName() + "!"
        );
        return "redirect:/staff/dashboard";
    }
}
