package com.ev.batteryswap.controllers.user;

import com.ev.batteryswap.controllers.AuthController;
import com.ev.batteryswap.pojo.Battery;
import com.ev.batteryswap.pojo.Phuongxa;
import com.ev.batteryswap.pojo.Quanhuyen;
import com.ev.batteryswap.pojo.Station;
import com.ev.batteryswap.pojo.Tinhthanh;
import com.ev.batteryswap.repositories.PhuongxaRepository;
import com.ev.batteryswap.repositories.QuanhuyenRepository;
import com.ev.batteryswap.repositories.TinhthanhRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private TinhthanhRepository tinhthanhRepository;

    @Autowired
    private QuanhuyenRepository quanhuyenRepository;

    @Autowired
    private PhuongxaRepository phuongxaRepository;


    @GetMapping("/location/tinhthanh")
    public ResponseEntity<List<Map<String, Object>>> getAllTinhthanh() {
        List<Map<String, Object>> result = tinhthanhRepository.findAll().stream()
                .map(t -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", t.getId());
                    m.put("ten", t.getTinhthanhcol());
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/location/quanhuyen/{tinhId}")
    public ResponseEntity<List<Map<String, Object>>> getQuanhuyenByTinh(@PathVariable("tinhId") Integer tinhId) {
        Tinhthanh tinh = tinhthanhRepository.findById(tinhId).orElse(null);
        if (tinh == null) return ResponseEntity.ok(List.of());
        List<Map<String, Object>> result = quanhuyenRepository.findByIdtinhthanh(tinh).stream()
                .map(q -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", q.getId());
                    m.put("ten", q.getTenquanhuyen());
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/location/phuongxa/{huyenId}")
    public ResponseEntity<List<Map<String, Object>>> getPhuongxaByHuyen(@PathVariable("huyenId") Integer huyenId) {
        Quanhuyen huyen = quanhuyenRepository.findById(huyenId).orElse(null);
        if (huyen == null) return ResponseEntity.ok(List.of());
        List<Map<String, Object>> result = phuongxaRepository.findByIdquanhuyen(huyen).stream()
                .map(p -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", p.getId());
                    m.put("ten", p.getTenphuongxa());
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stations/by-xa/{xaId}")
    public ResponseEntity<List<Map<String, Object>>> getStationsByXa(@PathVariable("xaId") Integer xaId) {
        Phuongxa xa = phuongxaRepository.findById(xaId).orElse(null);
        if (xa == null) return ResponseEntity.ok(List.of());
        
        List<Station> stations = stationService.getActiveStations().stream()
                .filter(s -> s.getPhuongxa() != null && s.getPhuongxa().getId().equals(xaId))
                .collect(Collectors.toList());
                
        List<Map<String, Object>> result = stations.stream()
                .map(s -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", s.getId());
                    m.put("name", s.getName());
                    m.put("address", s.getAddress());
                    m.put("quanId", s.getQuan() != null ? s.getQuan().getId() : null);
                    m.put("quanName", s.getQuan() != null ? s.getQuan().getTenquanhuyen() : null);
                    m.put("xaId", s.getPhuongxa() != null ? s.getPhuongxa().getId() : null);
                    m.put("xaName", s.getPhuongxa() != null ? s.getPhuongxa().getTenphuongxa() : null);
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
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
