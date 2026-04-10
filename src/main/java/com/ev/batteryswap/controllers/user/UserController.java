package com.ev.batteryswap.controllers.user;

import com.ev.batteryswap.dto.UserProfileDTO;
import com.ev.batteryswap.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users/123
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable int id) {
        return userService
            .findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/users/username/john
    @GetMapping("/username/{username}")
    public ResponseEntity<UserProfileDTO> getUserByUsername(
        @PathVariable String username
    ) {
        return userService
            .findByUsername(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
