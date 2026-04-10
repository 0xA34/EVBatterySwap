package com.ev.batteryswap.controllers.staff;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffController {

    @GetMapping("/staff")
    public String dashboard() {
        return "staff/dashboard";
    }

    @GetMapping("/staff/login")
    public String index() {
        return "staff/login";
    }

}
