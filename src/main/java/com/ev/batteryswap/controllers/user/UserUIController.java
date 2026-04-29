package com.ev.batteryswap.controllers.user;
import com.ev.batteryswap.controllers.AuthController;
import com.ev.batteryswap.dto.UserProfileDTO;
import com.ev.batteryswap.security.JwtCookieHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.ev.batteryswap.security.JwtTokenProvider;
import com.ev.batteryswap.services.UserService;
import com.ev.batteryswap.services.interfaces.IStationService;
import com.ev.batteryswap.services.interfaces.IBatteryService;
import java.util.Optional;

@Controller
public class UserUIController {

    private final JwtCookieHelper jwtCookieHelper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private IStationService stationService;

    @Autowired
    private IBatteryService batteryService;

    public UserUIController(JwtCookieHelper jwtCookieHelper) {
        this.jwtCookieHelper = jwtCookieHelper;
    }


    public void show_info(Model model, String token) {

        String username = jwtTokenProvider.extractUsername(token); // lấy username từ token jwt
        Optional<UserProfileDTO> user = userService.findByUsername(username);
        model.addAttribute("username", user.get().getUsername());
        model.addAttribute("walletBalance", user.get().getWalletBalance());
        model.addAttribute("InfoUser", user);
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


    @GetMapping("/user/topup")
    public String topupPage(HttpServletRequest request, Model model) {
        String token = jwtCookieHelper.extractCookieToken(
                request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            show_info(model, token);
            return "user/topup";
        }
        return "login";
    }

    @GetMapping("/user/dashboard")
    public String dashboardPage(
            @RequestParam(required = false) Integer tinhId,
            @RequestParam(required = false) Integer huyenId,
            @RequestParam(required = false) Integer xaId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size,
            HttpServletRequest request, Model model) {
        String token = jwtCookieHelper.extractCookieToken(
                request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            show_info(model, token);
            
            org.springframework.data.domain.Page<com.ev.batteryswap.pojo.Station> stationPage = 
                    stationService.filterStationsByLocation(tinhId, huyenId, xaId, org.springframework.data.domain.PageRequest.of(page - 1, size));
            
            java.util.Map<Integer, java.util.Map<String, Long>> batteryStatsByStation = new java.util.HashMap<>();
            for (com.ev.batteryswap.pojo.Station station : stationPage.getContent()) {
                batteryStatsByStation.put(station.getId(), batteryService.getBatteryStatisticsForStation(station));
            }
            
            model.addAttribute("stations", stationPage.getContent());
            model.addAttribute("batteryStatsByStation", batteryStatsByStation);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", stationPage.getTotalPages());
            
            // Retain search parameters
            model.addAttribute("tinhId", tinhId);
            model.addAttribute("huyenId", huyenId);
            model.addAttribute("xaId", xaId);

            return "user/dashboard";
        }
        return "login";
    }

}
