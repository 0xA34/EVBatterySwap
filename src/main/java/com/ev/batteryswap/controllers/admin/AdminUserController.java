package com.ev.batteryswap.controllers.admin;

import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "15") int size,
                            @RequestParam(required = false) String search) {
        Page<User> userPage = userService.filterUsers(search, PageRequest.of(page, size));
        model.addAttribute("userPage", userPage);
        model.addAttribute("search", search);
        return "admin/users";
    }

    @GetMapping("/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/users_form";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        try {
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm người dùng mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm người dùng: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findById(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "admin/users_form";
    }


    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Integer id,
                             @ModelAttribute("user") User userFormData,
                             RedirectAttributes redirectAttributes) {
        try {
            User existingUser = userService.findById(id);
            if (existingUser == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
                return "redirect:/admin/users";
            }

            existingUser.setUsername(userFormData.getUsername());
            existingUser.setFullName(userFormData.getFullName());
            existingUser.setEmail(userFormData.getEmail() != null && userFormData.getEmail().trim().isEmpty() ? null : userFormData.getEmail());
            existingUser.setPhoneNumber(userFormData.getPhoneNumber() != null && userFormData.getPhoneNumber().trim().isEmpty() ? null : userFormData.getPhoneNumber());
            existingUser.setRole(userFormData.getRole());

            // Nếu role không phải staff, tự động gán trạm là null
//            if (!"STAFF".equals(userFormData.getRole())) {
//                existingUser.setStation(null);
//            } else {
//                existingUser.setStation(userFormData.getStation());
//            }

            userService.saveUser(existingUser);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật người dùng thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật người dùng: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa người dùng: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}