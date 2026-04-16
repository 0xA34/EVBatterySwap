package com.ev.batteryswap.controllers.user;

import com.ev.batteryswap.controllers.AuthController;
import com.ev.batteryswap.pojo.Battery;
import com.ev.batteryswap.pojo.Station;
import com.ev.batteryswap.security.JwtCookieHelper;
import com.ev.batteryswap.security.JwtTokenProvider;
import com.ev.batteryswap.services.BatteryService;
import com.ev.batteryswap.services.ReservationService;
import com.ev.batteryswap.services.StationService;
import com.ev.batteryswap.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/reservations")
public class UserReservationController {

    @Autowired
    private StationService stationService;

    @Autowired
    private BatteryService batteryService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private JwtCookieHelper jwtCookieHelper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @GetMapping("/stations")
    public ResponseEntity<List<Station>> getActiveStations() {
        return ResponseEntity.ok(stationService.getActiveStations());
    }

    @GetMapping("/stations/{id}/batteries")
    public ResponseEntity<List<Battery>> getAvailableBatteries(@PathVariable("id") Integer stationId) {
        return ResponseEntity.ok(batteryService.getAvailableBatteriesByStation(stationId));
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookReservation(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        String token = jwtCookieHelper.extractCookieToken(request, AuthController.COOKIE_NAME);
        if (token == null || !jwtCookieHelper.isValidRoleToken(token, "DRIVER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Bạn cần đăng nhập với vai trò tài xế để đặt lịch.", "success", false));
        }

        try {
            String username = jwtTokenProvider.extractUsername(token);
            com.ev.batteryswap.dto.UserProfileDTO userDto = userService.findByUsername(username).orElse(null);
            
            if (userDto == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Người dùng không tồn tại.", "success", false));
            }

            Integer userId = userDto.getId();
            Integer stationId = Integer.valueOf(payload.get("stationId").toString());
            Integer batteryId = Integer.valueOf(payload.get("batteryId").toString());
            String pickupTimeStr = payload.get("pickupTime").toString();

            // validate time
            Instant pickupTime = LocalDateTime.parse(pickupTimeStr)
                .atZone(ZoneId.systemDefault())
                .toInstant();
                
            if (pickupTime.isBefore(Instant.now())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Thời gian đón không được nằm trong quá khứ.", "success", false));
            }

            reservationService.createReservation(userId, stationId, batteryId, pickupTimeStr);
            
            return ResponseEntity.ok(Map.of("message", "Đặt lịch thành công!", "success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "success", false));
        }
    }
}
