package com.ev.batteryswap.controllers.admin;

import com.ev.batteryswap.pojo.Battery;
import com.ev.batteryswap.pojo.Station;
import com.ev.batteryswap.repositories.BatteryRepository;
import com.ev.batteryswap.services.interfaces.IBatteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/batteries")
public class AdminBatteryController {
    @Autowired
    private IBatteryService batteryService;

    @ModelAttribute("stations")
    public List<Station> getAllStations() {
        return batteryService.getAllStations();
    }

    @GetMapping
    public String listBatteries(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(required = false) Integer stationId,
                                @RequestParam(required = false) String status,
                                @RequestParam(required = false) String search) {

        Page<Battery> batteryPage = batteryService.filterBatteries(stationId, status, search, PageRequest.of(page, 10));
        model.addAttribute("batteryPage", batteryPage);
        model.addAttribute("stats", batteryService.getBatteryStatistics());
        model.addAttribute("selectedStationId", stationId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("search", search);

        return "admin/battery_inventory";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("battery", new Battery());
        return "admin/battery_form";
    }

    @PostMapping
    public String createBattery(@ModelAttribute("battery") Battery battery, RedirectAttributes redirectAttributes) {
        batteryService.saveBattery(battery);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm pin mới thành công!");
        return "redirect:/admin/batteries";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Battery battery = batteryService.getBatteryById(id);
        if (battery != null) {
            model.addAttribute("battery", battery);
            return "admin/battery_form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy pin!");
            return "redirect:/admin/batteries";
        }
    }

    @PostMapping("/update/{id}")
    public String updateBattery(@PathVariable("id") Integer id,
                                @ModelAttribute("battery") Battery batteryFormData,
                                RedirectAttributes redirectAttributes) {

        try {
            Battery existingBattery = batteryService.getBatteryById(id);
            if (existingBattery == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy pin để cập nhật!");
                return "redirect:/admin/batteries";
            }

            // Cập nhật các trường từ form
            existingBattery.setStation(batteryFormData.getStation());
            existingBattery.setSerialNumber(batteryFormData.getSerialNumber());
            existingBattery.setModel(batteryFormData.getModel());
            existingBattery.setCapacityKwh(batteryFormData.getCapacityKwh());
            existingBattery.setCurrentChargePercentage(batteryFormData.getCurrentChargePercentage());
            existingBattery.setHealthPercentage(batteryFormData.getHealthPercentage());
            existingBattery.setStatus(batteryFormData.getStatus());

            // Lưu lại pin đã cập nhật
            batteryService.saveBattery(existingBattery);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật pin thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật pin: " + e.getMessage());
        }

        return "redirect:/admin/batteries";
    }

    @GetMapping("/delete/{id}")
    public String deleteBattery(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            batteryService.deleteBattery(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa pin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa pin!");
        }
        return "redirect:/admin/batteries";
    }

}
