package com.ev.batteryswap.controllers.staff;

import com.ev.batteryswap.controllers.AuthController;
import com.ev.batteryswap.security.JwtCookieHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.pojo.Station;
import com.ev.batteryswap.security.JwtTokenProvider;
import com.ev.batteryswap.services.interfaces.IBatteryService;
import com.ev.batteryswap.services.interfaces.IUserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.pojo.Station;
import com.ev.batteryswap.security.JwtTokenProvider;
import com.ev.batteryswap.services.interfaces.IBatteryService;
import com.ev.batteryswap.services.interfaces.IUserService;
import org.springframework.ui.Model;
import java.util.Map;
import jakarta.servlet.http.Cookie;

@Controller
@RequestMapping("/staff")
public class StaffUIController {
    @Autowired
    private JwtCookieHelper jwtCookieHelper;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private IUserService userService;
    @Autowired
    private IBatteryService batteryService;


    private User getCurrentStaffUser(HttpServletRequest request) {
        String token = jwtCookieHelper.extractCookieToken(request, AuthController.COOKIE_NAME);
        if (token == null || !jwtCookieHelper.isValidRoleToken(token, "STAFF")) {
            return null;
        }
        String username = jwtTokenProvider.extractUsername(token);
        return userService.findUserByUsername(username);
    }

    private Station getActiveStation(HttpServletRequest request, User staff) {
        if (staff == null || staff.getStations() == null || staff.getStations().isEmpty()) return null;
        
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("active_station_id".equals(c.getName())) {
                    try {
                        Integer stationId = Integer.parseInt(c.getValue());
                        for (Station s : staff.getStations()) {
                            if (s.getId().equals(stationId)) return s;
                        }
                    } catch (Exception e) {}
                }
            }
        }
        return staff.getStations().get(0);
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request) {
        String token = jwtCookieHelper.extractCookieToken(
                request,
                AuthController.COOKIE_NAME
        );
//        String token = jwtCookieHelper.extractCookieToken(request, "staff_token");
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "STAFF")) {
            return "redirect:/staff/dashboard";
        }
        return "staff/login";
    }

    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(HttpServletRequest request, Model model) {
        User staff = getCurrentStaffUser(request);
        Station station = getActiveStation(request, staff);
        
        if (staff == null || station == null) {
            return "redirect:/staff/login";
        }
        
        model.addAttribute("station", station);
        model.addAttribute("staff", staff);
        
        Map<String, Long> batteryStats = batteryService.getBatteryStatisticsForStation(station);
        model.addAttribute("totalBatteries", batteryStats.getOrDefault("total", 0L));
        model.addAttribute("availableBatteries", batteryStats.getOrDefault("available", 0L));
        model.addAttribute("chargingBatteries", batteryStats.getOrDefault("charging", 0L));
        model.addAttribute("maintenanceBatteries", batteryStats.getOrDefault("maintenance", 0L));

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
