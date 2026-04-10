package com.ev.batteryswap.controllers.admin;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String showAdminPage() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/login")
    public String loginAdminPage() {
        return "admin/login";
    }


}
