package com.ev.batteryswap.controllers.staff;

import com.ev.batteryswap.pojo.Battery;
import com.ev.batteryswap.pojo.Station;
import com.ev.batteryswap.pojo.SwapTransaction;
import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.security.JwtCookieHelper;
import com.ev.batteryswap.security.JwtTokenProvider;
import com.ev.batteryswap.services.interfaces.IBatteryService;
import com.ev.batteryswap.services.interfaces.IMaintenanceLogService;
import com.ev.batteryswap.services.interfaces.ITransactionService;
import com.ev.batteryswap.services.interfaces.IUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private IBatteryService batteryService;

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IMaintenanceLogService maintenanceLogService;

    @Autowired
    private JwtCookieHelper jwtCookieHelper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User getCurrentStaffUser(HttpServletRequest request) {
        String token = jwtCookieHelper.extractCookieToken(request, "staff_token");
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

    private String checkStaffStation(Station station, RedirectAttributes redirectAttributes) {
        if (station == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tài khoản nhân viên chưa được gán trạm hoặc trạm không hợp lệ.");
            return "redirect:/staff/login";
        }
        return null;
    }

    @PostMapping("/switch-station")
    public String switchStation(@RequestParam("stationId") Integer stationId,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                RedirectAttributes redirectAttributes) {
        User staff = getCurrentStaffUser(request);
        if (staff != null && staff.getStations() != null) {
            boolean valid = staff.getStations().stream().anyMatch(s -> s.getId().equals(stationId));
            if (valid) {
                Cookie cookie = new Cookie("active_station_id", String.valueOf(stationId));
                cookie.setPath("/");
                cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
                response.addCookie(cookie);
                redirectAttributes.addFlashAttribute("successMessage", "Đã chuyển đổi sang trạm làm việc mới!");
            }
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/staff/dashboard");
    }

//    @GetMapping("/dashboard")
//    public String showDashboard(Model model, RedirectAttributes redirectAttributes) {
//        User staff = getCurrentStaffUser();
//        String redirect = checkStaffStation(staff, redirectAttributes);
//        if (redirect != null) return redirect;
//
//        model.addAttribute("station", staff.getStation());
//        model.addAttribute("stats", batteryService.getBatteryStatisticsForStation(staff.getStation()));
//        return "staff/dashboard";
//    }

    @GetMapping("/transactions")
    public String listTransactions(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(required = false) String paymentStatus,
                                   @RequestParam(required = false) String search,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request) {
        User staff = getCurrentStaffUser(request);
        Station station = getActiveStation(request, staff);
        String redirect = checkStaffStation(station, redirectAttributes);
        if (redirect != null) return redirect;

        Integer stationId = station.getId();
        Page<SwapTransaction> transactionPage = transactionService.filterTransactions(stationId, paymentStatus, search, PageRequest.of(page, 15));

        model.addAttribute("transactionPage", transactionPage);
        model.addAttribute("station", station);
        model.addAttribute("staff", staff);
        model.addAttribute("selectedPaymentStatus", paymentStatus);
        model.addAttribute("search", search);

        return "staff/transactions";
    }

    @GetMapping("/batteries")
    public String listBatteries(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(required = false) String status,
                                @RequestParam(required = false) String search,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
        User staff = getCurrentStaffUser(request);
        Station station = getActiveStation(request, staff);
        String redirect = checkStaffStation(station, redirectAttributes);
        if (redirect != null) return redirect;

        Integer stationId = station.getId();
        Page<Battery> batteryPage = batteryService.filterBatteries(stationId, status, search, PageRequest.of(page, 15));

        model.addAttribute("batteryPage", batteryPage);
        model.addAttribute("station", station);
        model.addAttribute("staff", staff);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("search", search);
        return "staff/battery_inventory";
    }

    @GetMapping("/transactions/new")
    public String showCreateTransactionForm(Model model, RedirectAttributes redirectAttributes,
                                            HttpServletRequest request) {
        User staff = getCurrentStaffUser(request);
        Station station = getActiveStation(request, staff);
        String redirect = checkStaffStation(station, redirectAttributes);
        if (redirect != null) return redirect;

        SwapTransaction transaction = new SwapTransaction();
        transaction.setStation(station);

        model.addAttribute("transaction", transaction);
        model.addAttribute("staff", staff);

        // 1. Pin cũ (khách trả vào): Lấy TẤT CẢ pin đang RENTED
        Page<Battery> rentedBatteries = batteryService.filterBatteries(null, "RENTED", null, PageRequest.of(0, 1000));
        model.addAttribute("rentedBatteries", rentedBatteries.getContent());

        // 2. Pin mới (đưa cho khách): Lấy pin AVAILABLE tại trạm của staff
        Integer stationId = station.getId();
        Page<Battery> availableBatteries = batteryService.filterBatteries(stationId, "AVAILABLE", null, PageRequest.of(0, 1000));
        model.addAttribute("availableBatteries", availableBatteries.getContent());

        try {
            // Lấy user có role driver
            List<User> drivers = userService.getUsersByRole("DRIVER");
            model.addAttribute("users", drivers);
        } catch (Exception e) {
            model.addAttribute("users", Collections.emptyList());
        }

        return "staff/transaction_form";
    }

    @PostMapping("/transactions")
    public String createTransaction(@ModelAttribute("transaction") SwapTransaction transaction,
                                    RedirectAttributes redirectAttributes,
                                    HttpServletRequest request) {
        User staff = getCurrentStaffUser(request);
        Station station = getActiveStation(request, staff);
        String redirect = checkStaffStation(station, redirectAttributes);
        if (redirect != null) return redirect;

        transaction.setStation(station);

        try {
            transactionService.createTransaction(transaction);
            redirectAttributes.addFlashAttribute("successMessage", "Xác nhận đổi pin và ghi nhận giao dịch thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/staff/transactions";
    }

    @PostMapping("/batteries/report")
    public String reportBatteryIssue(@RequestParam("batteryId") Integer batteryId,
                                     @RequestParam("reason") String reason,
                                     RedirectAttributes redirectAttributes,
                                     HttpServletRequest request) {
        User staff = getCurrentStaffUser(request);

        try {
            maintenanceLogService.reportIssue(batteryId, reason, staff);
            redirectAttributes.addFlashAttribute("successMessage", "Đã báo hỏng pin thành công. Pin đã chuyển sang chế độ Bảo trì.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }

        return "redirect:/staff/batteries"; // Quay lại trang danh sách pin
    }
}

