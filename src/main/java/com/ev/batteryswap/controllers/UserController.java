package com.ev.batteryswap.controllers;

import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/find/{username}")
    public User getUserRepository(@PathVariable String username)  {
        return userRepository.findByUsername(username);
    }

    @GetMapping("/find/{id}")
    public User getUserRepository(@PathVariable int id)  {
        return userRepository.findById(id);
    }


}
