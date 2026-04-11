package com.ev.batteryswap.controllers.user;

import com.ev.batteryswap.controllers.AuthController;
import com.ev.batteryswap.security.JwtCookieHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.ev.batteryswap.security.JwtCookieHelper;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class UserUIController {

    private final JwtCookieHelper jwtCookieHelper;

    public UserUIController(JwtCookieHelper jwtCookieHelper) {
        this.jwtCookieHelper = jwtCookieHelper;
    }

    @GetMapping("/my")
    public String loginPage(HttpServletRequest request) {
        String token = jwtCookieHelper.extractCookieToken(
                request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            return "user/my";
        }
        return "login";
    }


}
