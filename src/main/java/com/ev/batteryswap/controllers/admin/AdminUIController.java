package com.ev.batteryswap.controllers.admin;

import com.ev.batteryswap.controllers.AuthController;
import com.ev.batteryswap.security.JwtCookieHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import com.ev.batteryswap.services.interfaces.ITransactionService;
import com.ev.batteryswap.services.interfaces.IStationService;
import com.ev.batteryswap.services.interfaces.IBatteryService;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminUIController {
    @Autowired
    private JwtCookieHelper jwtCookieHelper;
    @Autowired
    private ITransactionService transactionService;
    @Autowired
    private IStationService stationService;
    @Autowired
    private IBatteryService batteryService;

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
    public String dashboard(Model model) {
        // 1. Tổng doanh thu
        BigDecimal totalRevenue = transactionService.getTotalRevenue();
        model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // 2. Lượt đổi pin
        Map<String, Long> transStats = transactionService.getTransactionStatistics();
        model.addAttribute("totalSwaps", transStats.getOrDefault("total_transactions", 0L));

        // 3. Trạm hoạt động
        Map<String, Long> stationStats = stationService.getStationStatistics();
        model.addAttribute("activeStations", stationStats.getOrDefault("active_stations", 0L));

        // 4. Pin cần bảo dưỡng
        Map<String, Long> batteryStats = batteryService.getBatteryStatistics();
        model.addAttribute("maintenanceBatteries", batteryStats.getOrDefault("maintenance", 0L));

        // 5. Biểu đồ tần suất đổi pin
        List<Map<String, Object>> hourlySwapReport = transactionService.getHourlySwapReport();
        model.addAttribute("hourlySwapReport", hourlySwapReport);

        return "admin/dashboard";
    }

    @GetMapping("/logout")
    public String logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        jwtCookieHelper.revokeAndExpireCookie(request, response, "admin_token", "/admin");
        return "redirect:/admin/login";
    }

}
