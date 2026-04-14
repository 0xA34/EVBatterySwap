package com.ev.batteryswap.controllers.admin;

import com.ev.batteryswap.pojo.RentalPackage;
import com.ev.batteryswap.services.interfaces.IRentalPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/packages")
public class AdminRentalPackageController {

    @Autowired
    private IRentalPackageService packageService;

    @GetMapping
    public String listPackages(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<RentalPackage> packagePage = packageService.findAll(PageRequest.of(page, 15));
        model.addAttribute("packagePage", packagePage);
        model.addAttribute("stats", packageService.getStatistics());
        return "admin/rental_packages";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("package", new RentalPackage());
        return "admin/package_form";
    }

    @PostMapping
    public String savePackage(@ModelAttribute("package") RentalPackage packageFormData, RedirectAttributes redirectAttributes) {
        try {
            String successMessage;
            if (packageFormData.getId() == null) {
                // 1. LOGIC TẠO MỚI
                packageService.save(packageFormData);
                successMessage = "Tạo gói thuê mới thành công!";
            } else {
                RentalPackage existingPackage = packageService.findById(packageFormData.getId());
                if (existingPackage == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: Không tìm thấy gói thuê để cập nhật!");
                    return "redirect:/admin/packages";
                }
                // Cập nhật các trường từ form
                existingPackage.setName(packageFormData.getName());
                existingPackage.setDescription(packageFormData.getDescription());
                existingPackage.setPrice(packageFormData.getPrice());
                existingPackage.setDurationDays(packageFormData.getDurationDays());
                existingPackage.setStatus(packageFormData.getStatus());

                // Lưu lại đối tượng đã cập nhật
                packageService.save(existingPackage);
                successMessage = "Cập nhật gói thuê thành công!";
            }

            redirectAttributes.addFlashAttribute("successMessage", successMessage);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu gói thuê: " + e.getMessage());
        }

        return "redirect:/admin/packages";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        RentalPackage rentalPackage = packageService.findById(id);
        if (rentalPackage != null) {
            model.addAttribute("package", rentalPackage);
            return "admin/package_form";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy gói thuê!");
        return "redirect:/admin/packages";
    }

    @GetMapping("/delete/{id}")
    public String deletePackage(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        packageService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa gói thuê thành công!");
        return "redirect:/admin/packages";
    }
}
