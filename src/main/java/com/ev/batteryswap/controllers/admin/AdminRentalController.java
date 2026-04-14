package com.ev.batteryswap.controllers.admin;

import com.ev.batteryswap.pojo.Rental;
import com.ev.batteryswap.services.interfaces.IRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/rentals")
public class AdminRentalController {

    @Autowired
    private IRentalService rentalService;

    @GetMapping
    public String listRentals(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) String search) {

        Page<Rental> rentalPage = rentalService.filterRentals(search, PageRequest.of(page, 15));

        model.addAttribute("rentalPage", rentalPage);
        model.addAttribute("search", search);
        return "admin/rentals";
    }

    @GetMapping("/delete/{id}")
    public String deleteRental(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            rentalService.deleteRental(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa biên lai thuê gói thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa biên lai: " + e.getMessage());
        }
        return "redirect:/admin/rentals";
    }
}
