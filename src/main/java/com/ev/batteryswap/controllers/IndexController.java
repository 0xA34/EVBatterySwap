package com.ev.batteryswap.controllers;


import com.ev.batteryswap.security.JwtCookieHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final JwtCookieHelper jwtCookieHelper;

    public IndexController(JwtCookieHelper jwtCookieHelper) {

        this.jwtCookieHelper = jwtCookieHelper;
    }

    @GetMapping("/user/login")
    public String showLoginPage(HttpServletRequest request) {

        String token = jwtCookieHelper.extractCookieToken(
                request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            return "redirect:/my";
        }
        return "login";
    }


    @GetMapping("/user/packages")
    public String userPackagesPage(HttpServletRequest request) {

        String token = jwtCookieHelper.extractCookieToken(
                request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            return "user/packages";
        }
        return "packages";
    }


    @GetMapping("/user/register")
    public String showRegisterPage(HttpServletRequest request) {

        String token = jwtCookieHelper.extractCookieToken(
                request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            return "redirect:/my";
        }
        return "register";
    }

    @GetMapping("/")
    public String showHomePage(HttpServletRequest request) {
        String token = jwtCookieHelper.extractCookieToken(
                request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            return "redirect:/my";
        }
        return "index";
    }

}
