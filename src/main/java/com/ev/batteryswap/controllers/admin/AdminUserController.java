package com.ev.batteryswap.controllers.admin;

import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.services.UserService;
import com.ev.batteryswap.services.interfaces.IStationService;
import com.ev.batteryswap.pojo.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final IStationService stationService;

    public AdminUserController(
        UserService userService,
        PasswordEncoder passwordEncoder,
        IStationService stationService
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.stationService = stationService;
    }

    @GetMapping
    public String listUsers(
        Model model,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size,
        @RequestParam(required = false) String search
    ) {
        Page<User> userPage = userService.filterUsers(
            search,
            PageRequest.of(page, size)
        );
        model.addAttribute("userPage", userPage);
        model.addAttribute("search", search);
        return "admin/users";
    }

    @GetMapping("/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allStations", stationService.getActiveStations());
        return "admin/users_form";
    }

    @PostMapping
    public String createUser(
        @ModelAttribute("user") User user,
        @RequestParam(value = "stationIds", required = false) List<Integer> stationIds,
        RedirectAttributes redirectAttributes
    ) {
        try {
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            if ("STAFF".equals(user.getRole()) && stationIds != null) {
                List<Station> stations = new ArrayList<>();
                for (Integer sid : stationIds) {
                    Station s = stationService.getStationById(sid);
                    if (s != null) stations.add(s);
                }
                user.setStations(stations);
            }
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Thêm người dùng mới thành công!"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Lỗi khi thêm người dùng: " + e.getMessage()
            );
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(
        @PathVariable("id") Integer id,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        User user = userService.findById(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Không tìm thấy người dùng!"
            );
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("allStations", stationService.getActiveStations());
        return "admin/users_form";
    }

    @PostMapping("/update/{id}")
    public String updateUser(
        @PathVariable("id") Integer id,
        @ModelAttribute("user") User userFormData,
        @RequestParam(value = "stationIds", required = false) List<Integer> stationIds,
        RedirectAttributes redirectAttributes
    ) {
        try {
            User existingUser = userService.findById(id);
            if (existingUser == null) {
                redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Không tìm thấy người dùng!"
                );
                return "redirect:/admin/users";
            }

            existingUser.setUsername(userFormData.getUsername());
            existingUser.setFullName(userFormData.getFullName());
            existingUser.setEmail(
                userFormData.getEmail() != null &&
                    userFormData.getEmail().trim().isEmpty()
                    ? null
                    : userFormData.getEmail()
            );
            existingUser.setPhoneNumber(
                userFormData.getPhoneNumber() != null &&
                    userFormData.getPhoneNumber().trim().isEmpty()
                    ? null
                    : userFormData.getPhoneNumber()
            );
            existingUser.setRole(userFormData.getRole());

            if ("STAFF".equals(userFormData.getRole())) {
                List<Station> newStations = new ArrayList<>();
                if (stationIds != null) {
                    for (Integer sid : stationIds) {
                        Station s = stationService.getStationById(sid);
                        if (s != null) newStations.add(s);
                    }
                }
                existingUser.setStations(newStations);
            } else {
                existingUser.getStations().clear();
            }

            userService.saveUser(existingUser);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Cập nhật người dùng thành công!"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Lỗi khi cập nhật người dùng: " + e.getMessage()
            );
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(
        @PathVariable("id") Integer userId,
        RedirectAttributes redirectAttributes
    ) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Xóa người dùng thành công!"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Lỗi khi xóa người dùng: " + e.getMessage()
            );
        }
        return "redirect:/admin/users";
    }
}
