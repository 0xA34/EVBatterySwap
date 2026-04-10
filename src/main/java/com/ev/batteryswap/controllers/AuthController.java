package com.ev.batteryswap.controllers;
import com.ev.batteryswap.dto.APIResponse;
import com.ev.batteryswap.dto.AuthRequestLogin;
import com.ev.batteryswap.dto.AuthRequestRegister;
import com.ev.batteryswap.dto.AuthResponse;
import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.repositories.UserRepository;
import com.ev.batteryswap.security.JwtCookieHelper;
import com.ev.batteryswap.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtCookieHelper jwtCookieHelper;

    public AuthController(
            AuthService authService,
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

    public static String COOKIE_NAME = "";
    public static String COOKIE_PATH = "";

    @PostMapping("/login")
    public ResponseEntity<?> Login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response
    ) {
        User user = userRepository.findByUsername(username);

        if (
                user == null ||
                        !passwordEncoder.matches(password, user.getPassword())
        ) {
            return ResponseEntity.badRequest().body("Tài Khoản Hoặc Mật Khẩu Không Đúng!");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(
                username
        );
        String token = jwtCookieHelper.generateRoleToken(
                userDetails,
                user.getRole(),
                user.getUsername()
        );

        if (user.getRole().equals("ADMIN")) {
            COOKIE_NAME = "admin_token";
            COOKIE_PATH = "/admin";
            jwtCookieHelper.setTokenCookie(
                    response,
                    COOKIE_NAME,
                    token,
                    COOKIE_PATH
            );
            return ResponseEntity.ok("Đăng Nhập Thành Công");
        }
        else if (user.getRole().equals("STAFF")) {
            COOKIE_NAME = "staff_token";
            COOKIE_PATH = "/staff";
            jwtCookieHelper.setTokenCookie(
                    response,
                    COOKIE_NAME,
                    token,
                    COOKIE_PATH
            );
            return ResponseEntity.ok("Đăng Nhập Thành Công");
        }
        else if (user.getRole().equals("DRIVER")) {
            COOKIE_NAME = "driver_token";
            COOKIE_PATH = "/";
            jwtCookieHelper.setTokenCookie(
                    response,
                    COOKIE_NAME,
                    token,
                    COOKIE_PATH
            );
            return ResponseEntity.ok("Đăng Nhập Thành Công");
        }
        else {
            return ResponseEntity.badRequest().body("Đăng Nhập Thất Bại");
        }
    }

    @GetMapping("/logout")
    public void Logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        jwtCookieHelper.revokeAndExpireCookie(
                request,
                response,
                AuthController.COOKIE_NAME,
                AuthController.COOKIE_PATH
        );
        response.sendRedirect("/");
    }

}
