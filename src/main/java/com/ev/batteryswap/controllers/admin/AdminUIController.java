package com.ev.batteryswap.controllers.admin;

import com.ev.batteryswap.controllers.AuthController;
import com.ev.batteryswap.security.JwtCookieHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminUIController {

    private final JwtCookieHelper jwtCookieHelper;

    public AdminUIController(JwtCookieHelper jwtCookieHelper) {
        this.jwtCookieHelper = jwtCookieHelper;
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        String token = jwtCookieHelper.extractCookieToken(
            request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        return "admin/login";
    }

    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(HttpServletRequest request) {
        String token = jwtCookieHelper.extractCookieToken(
            request,
            AuthController.COOKIE_NAME
        );
        if (
            token == null || !jwtCookieHelper.isValidRoleToken(token, "ADMIN")
        ) {
            return "redirect:/admin/login";
        }
        return "admin/dashboard";
    }

}
