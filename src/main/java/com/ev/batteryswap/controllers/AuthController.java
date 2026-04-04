package com.ev.batteryswap.controllers;

import com.ev.batteryswap.dto.APIResponse;
import com.ev.batteryswap.dto.AuthRequestLogin;
import com.ev.batteryswap.dto.AuthRequestRegister;
import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register API.
    @PostMapping("/register")
    public ResponseEntity<?> register(@ModelAttribute AuthRequestRegister authRequestRegister) {
        if (!authRequestRegister.getPassword().equals(authRequestRegister.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new APIResponse(false, "Passwords do not match!"));
        }

        if (authService.existsByUsername(authRequestRegister.getUsername())) {
            return ResponseEntity.badRequest().body(new APIResponse(false, "Username already exists"));
        }

        User user = new User();
        user.setUsername(authRequestRegister.getUsername());
        user.setPassword(authRequestRegister.getPassword());
        user.setFullName(authRequestRegister.getFullName());
        authService.register(user);

        return ResponseEntity.ok(new APIResponse(true, "Registration Successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@ModelAttribute AuthRequestLogin authRequestLogin) {
        boolean isAuthenticated = authService.login(
                authRequestLogin.getUsername(),
                authRequestLogin.getPassword()
        );

        if (isAuthenticated) {
            return ResponseEntity.ok(new APIResponse(true, "Login Successfully"));
        } else  {
            return ResponseEntity.badRequest().body(new APIResponse(false, "Login Failed"));
        }
    }
}
