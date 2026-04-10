package com.ev.batteryswap.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/user/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/user/register")
    public String showRegisterPage() {
        return "register";
    }

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

}
