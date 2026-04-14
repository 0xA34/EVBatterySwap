package com.ev.batteryswap.controllers.admin;

import com.ev.batteryswap.pojo.Battery;
import com.ev.batteryswap.pojo.MaintenanceLog;
import com.ev.batteryswap.repositories.BatteryRepository;
import com.ev.batteryswap.repositories.MaintenanceLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/maintenance")
public class AdminMaintenanceController {

    @Autowired
    private MaintenanceLogRepository maintenanceLogRepository;

    @Autowired
    private BatteryRepository batteryRepository;

    @GetMapping
    public String listMaintenanceLogs(Model model, @RequestParam(defaultValue = "0") int page) {
        // Lấy danh sách sắp xếp theo ngày mới nhất
        Page<MaintenanceLog> logPage = maintenanceLogRepository.findAll(
                PageRequest.of(page, 15, Sort.by("createdAt").descending())
        );
        model.addAttribute("logPage", logPage);
        return "admin/maintenance_logs.html";
    }

    @PostMapping("/complete")
    public String completeMaintenance(@RequestParam("batteryId") Integer batteryId,
                                      @RequestParam("cost") BigDecimal cost,
                                      @RequestParam("notes") String notes,
                                      RedirectAttributes redirectAttributes) {
        try {
            Battery battery = batteryRepository.findById(batteryId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy pin!"));

            // Kiểm tra xem pin có đang bảo trì không
            if (!"MAINTENANCE".equals(battery.getStatus())) {
                throw new RuntimeException("Pin này không ở trạng thái bảo trì.");
            }

            // 1. Cập nhật trạng thái pin -> AVAILABLE
            battery.setStatus("AVAILABLE");
            batteryRepository.save(battery);

            // 2. Ghi log: Đã sửa xong
            MaintenanceLog log = new MaintenanceLog();
            log.setBattery(battery);
            log.setMaintenanceType("REPAIR_COMPLETED"); // Loại: Sửa xong
            log.setDescription("Hoàn tất bảo trì: " + notes);
            log.setCost(cost);
            log.setTechnician("Admin"); // Hoặc lấy tên Admin đang đăng nhập
            log.setMaintenanceDate(LocalDate.now());

            maintenanceLogRepository.save(log);

            redirectAttributes.addFlashAttribute("successMessage", "Đã hoàn tất bảo trì. Pin đã sẵn sàng sử dụng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/batteries"; // Quay về kho pin để thấy nó xanh trở lại
    }
}
