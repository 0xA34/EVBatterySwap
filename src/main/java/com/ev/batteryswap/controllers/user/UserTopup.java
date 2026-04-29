package com.ev.batteryswap.controllers.user;
import com.ev.batteryswap.controllers.AuthController;
import com.ev.batteryswap.security.JwtCookieHelper;
import com.ev.batteryswap.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class UserTopup {

    @Autowired
    private JwtCookieHelper jwtCookieHelper;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    String stk = "22749061"; // số tài khoản ngân hàng
    String name_nganhang = "ACB"; // tên ngân hàng

    @PostMapping("/qr")
    public ResponseEntity<?> createQR(HttpServletRequest request,  @RequestParam BigDecimal amount) {

        String token = jwtCookieHelper.extractCookieToken(
                request,
                AuthController.COOKIE_NAME
        );
        if (token != null && jwtCookieHelper.isValidRoleToken(token, "DRIVER")){
            String username = jwtTokenProvider.extractUsername(token);
            String url = "https://img.vietqr.io/image/"+name_nganhang+"-"+stk+"-compact1.jpg?addInfo="+username+"&amount="+amount;
            return ResponseEntity.ok(url);
        }
        return ResponseEntity.badRequest().build();
    }

}
