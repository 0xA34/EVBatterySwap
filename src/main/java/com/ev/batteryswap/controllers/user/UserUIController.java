package com.ev.batteryswap.controllers.user;
import com.ev.batteryswap.controllers.AuthController;
import com.ev.batteryswap.dto.UserProfileDTO;
import com.ev.batteryswap.pojo.*;
import com.ev.batteryswap.security.JwtCookieHelper;
import com.ev.batteryswap.services.PaymentInfoService;
import com.ev.batteryswap.services.BatteryService;
import com.ev.batteryswap.services.StationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.ev.batteryswap.security.JwtTokenProvider;
import com.ev.batteryswap.services.UserService;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import com.ev.batteryswap.repositories.StationReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserUIController {

    private final JwtCookieHelper jwtCookieHelper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Autowired
    private BatteryService batteryService;

    @Autowired
    private StationService stationService;

    @Autowired
    private StationReviewRepository stationReviewRepository;

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


    @GetMapping("/user/history")
    public String historyPage(HttpServletRequest request, Model model, @RequestParam(defaultValue = "1") int page) {
        String token = jwtCookieHelper.extractCookieToken(
                request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            show_info(model, token);

            String username = jwtTokenProvider.extractUsername(token); // lấy username từ token jwt
            Optional<UserProfileDTO> user = userService.findByUsername(username);

            Page<PaymentInfo> paymentInfos = paymentInfoService.filterPaymentInfo(user.get().getId(), PageRequest.of(page-1, 15));

            model.addAttribute("payinfos", paymentInfos);

            return "user/history";
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

            Page<Station> stationPage =
                    stationService.filterStationsByLocation(tinhId, huyenId, xaId, PageRequest.of(page - 1, size));

            Map<Integer, Map<String, Long>> batteryStatsByStation = new java.util.HashMap<>();
            for (Station station : stationPage.getContent()) {
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

    @GetMapping("/user/station")
    public String stationPage(
            @RequestParam Integer id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request, Model model) {
        String token = jwtCookieHelper.extractCookieToken(request, AuthController.COOKIE_NAME);
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            show_info(model, token);

            Station station = stationService.getStationById(id);
            if (station == null) return "redirect:/user/dashboard";

            Page<Battery> batteryPage =
                    batteryService.filterBatteries(id, null, null, PageRequest.of(page - 1, size));

            model.addAttribute("stationId", station.getId());
            model.addAttribute("stationName", station.getName());
            model.addAttribute("stationAddress", station.getAddress());
            model.addAttribute("batteryPage", batteryPage);
            model.addAttribute("batteryStatsByStation", batteryService.getBatteryStatisticsForStation(station));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", batteryPage.getTotalPages());

            return "user/station";
        }
        return "login";
    }

    @PostMapping("/api/rating")
    @ResponseBody
    public ResponseEntity<?> addRating(
            @RequestParam("rating") Byte rating,
            @RequestParam(value = "comment", required = false) String comment,
            @RequestParam("station_id") Integer stationId,
            HttpServletRequest request) {

        String token = jwtCookieHelper.extractCookieToken(request, AuthController.COOKIE_NAME);
        if (token == null || !jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Vui lòng đăng nhập để đánh giá");
        }

        String username = jwtTokenProvider.extractUsername(token);
        User user = userService.findUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không tìm thấy người dùng");
        }

        Station station = stationService.getStationById(stationId);
        if (station == null) {
            return ResponseEntity.badRequest().body("Trạm không tồn tại");
        }

        if (stationReviewRepository.existsByUserAndStation(user, station)) {
            return ResponseEntity.badRequest().body("Bạn đã đánh giá trạm này rồi!");
        }

        StationReview review = new StationReview();
        review.setUser(user);
        review.setStation(station);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(Instant.now());

        stationReviewRepository.save(review);

        return ResponseEntity.ok("Cảm ơn bạn đã đánh giá!");
    }

}
