package com.ev.batteryswap.controllers;

import com.ev.batteryswap.dto.APIResponse;
import com.ev.batteryswap.dto.AuthRequestLogin;
import com.ev.batteryswap.dto.AuthRequestRegister;
import com.ev.batteryswap.dto.AuthResponse;
import com.ev.batteryswap.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @Valid @ModelAttribute AuthRequestLogin request
    ) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok("Đăng Nhập Thành Công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new APIResponse(false, "Invalid username or password")
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(
                new APIResponse(false, "No token provided")
            );
        }

        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(
            new APIResponse(true, "Logged out successfully")
        );
    }
}
