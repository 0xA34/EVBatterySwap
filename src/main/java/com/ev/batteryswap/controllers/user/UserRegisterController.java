package com.ev.batteryswap.controllers.user;

import com.ev.batteryswap.dto.APIResponse;
import com.ev.batteryswap.dto.AuthRequestRegister;
import com.ev.batteryswap.dto.AuthResponse;
import com.ev.batteryswap.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ev.batteryswap.services.AuthService;

@RestController
@RequestMapping("/api")
public class UserRegisterController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @ModelAttribute AuthRequestRegister request
    ) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(
                    new APIResponse(false, "Passwords do not match!")
            );
        }
        if (authService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(
                    new APIResponse(false, "Username already exists")
            );
        }
        if (authService.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(
                    new APIResponse(false, "Email already exists")
            );
        }
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok("Đăng Ký Thành Công");
    }


}
