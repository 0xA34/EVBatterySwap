package com.ev.batteryswap.controllers.admin;

import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.repositories.UserRepository;
import com.ev.batteryswap.security.JwtCookieHelper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    static final String COOKIE_NAME = "admin_token";
    static final String COOKIE_PATH = "/admin";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtCookieHelper jwtCookieHelper;

    public AdminLoginController(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        UserDetailsService userDetailsService,
        JwtCookieHelper jwtCookieHelper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtCookieHelper = jwtCookieHelper;
    }

    @PostMapping("/login")
    public String processLogin(
        @RequestParam String username,
        @RequestParam String password,
        HttpServletResponse response,
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

        UserDetails userDetails = userDetailsService.loadUserByUsername(
            username
        );
        String token = jwtCookieHelper.generateRoleToken(
            userDetails,
            user.getRole(),
            user.getUsername()
        );
        jwtCookieHelper.setTokenCookie(
            response,
            COOKIE_NAME,
            token,
            COOKIE_PATH
        );

        redirectAttributes.addFlashAttribute(
            "loginSuccess",
            "Đăng nhập thành công! Chào mừng, " + user.getFullName() + "!"
        );
        return "redirect:/admin/dashboard";
    }
}
