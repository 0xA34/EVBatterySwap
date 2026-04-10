package com.ev.batteryswap.controllers.staff;

import com.ev.batteryswap.controllers.AuthController;
import com.ev.batteryswap.security.JwtCookieHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffUIController {

    private final JwtCookieHelper jwtCookieHelper;

    public StaffUIController(JwtCookieHelper jwtCookieHelper) {
        this.jwtCookieHelper = jwtCookieHelper;
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        String token = jwtCookieHelper.extractCookieToken(
            request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "STAFF")) {
            return "redirect:/staff/dashboard";
        }
        return "staff/login";
    }

    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(HttpServletRequest request) {
        String token = jwtCookieHelper.extractCookieToken(
            request,
                AuthController.COOKIE_NAME
        );
        if (
            token == null || !jwtCookieHelper.isValidRoleToken(token, "STAFF")
        ) {
            return "redirect:/staff/login";
        }
        return "staff/dashboard";
    }

    @GetMapping("/logout")
    public String logout(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        jwtCookieHelper.revokeAndExpireCookie(
            request,
            response,
                AuthController.COOKIE_NAME,
                AuthController.COOKIE_PATH
        );
        return "redirect:/staff/login";
    }
}
