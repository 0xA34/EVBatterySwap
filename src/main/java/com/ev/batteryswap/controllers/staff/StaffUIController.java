package com.ev.batteryswap.controllers.staff;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffUIController {
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("staffUser") != null) {
            return "redirect:/staff/dashboard";
        }
        return "staff/login";
    }

    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(HttpSession session) {
        if (session.getAttribute("staffUser") == null) {
            return "redirect:/staff/login";
        }
        return "staff/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/staff/login";
    }
}
