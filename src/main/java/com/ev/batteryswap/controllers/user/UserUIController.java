package com.ev.batteryswap.controllers.user;
import com.ev.batteryswap.controllers.AuthController;
import com.ev.batteryswap.dto.UserProfileDTO;
import com.ev.batteryswap.security.JwtCookieHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.ev.batteryswap.security.JwtTokenProvider;
import com.ev.batteryswap.services.UserService;

import java.util.Optional;

@Controller

public class UserUIController {

    private final JwtCookieHelper jwtCookieHelper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    public UserUIController(JwtCookieHelper jwtCookieHelper) {
        this.jwtCookieHelper = jwtCookieHelper;
    }


    public void show_info(Model model, String token) {

        String username = jwtTokenProvider.extractUsername(token); // lấy username từ token jwt
        Optional<UserProfileDTO> user = userService.findByUsername(username);
        model.addAttribute("username", user.get().getUsername());
        model.addAttribute("walletBalance", user.get().getWalletBalance());
    }



    @GetMapping("/user/my")
    public String myPage(HttpServletRequest request, Model model) {
        String token = jwtCookieHelper.extractCookieToken(
                request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            show_info(model, token);
            return "user/my";
        }
        return "login";
    }


    @GetMapping("/user/book")
    public String bookPage(HttpServletRequest request, Model model) {
        String token = jwtCookieHelper.extractCookieToken(
                request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            show_info(model, token);
            return "user/book";
        }
        return "login";
    }


    @GetMapping("/user/profile")
    public String profilePage(HttpServletRequest request, Model model) {
        String token = jwtCookieHelper.extractCookieToken(
                request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            show_info(model, token);
            return "user/profile";
        }
        return "login";
    }


}
