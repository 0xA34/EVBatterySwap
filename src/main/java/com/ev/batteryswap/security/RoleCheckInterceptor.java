package com.ev.batteryswap.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor kiểm tra role trước khi cho phép truy cập các trang /admin/** và /staff/**.
 * Nếu token không hợp lệ hoặc không đúng role, redirect về trang login tương ứng.
 */
@Component
public class RoleCheckInterceptor implements HandlerInterceptor {

    private final JwtCookieHelper jwtCookieHelper;

    public RoleCheckInterceptor(JwtCookieHelper jwtCookieHelper) {
        this.jwtCookieHelper = jwtCookieHelper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // Kiểm tra Admin
        if (path.startsWith("/admin/") || path.equals("/admin")) {
            // Bỏ qua trang login
            if (path.equals("/admin/login")) {
                return true;
            }
            String token = jwtCookieHelper.extractCookieToken(request, "admin_token");
            if (token == null || !jwtCookieHelper.isValidRoleToken(token, "ADMIN")) {
                response.sendRedirect("/admin/login");
                return false;
            }
            return true;
        }

        // Kiểm tra Staff
        if (path.startsWith("/staff/") || path.equals("/staff")) {
            // Bỏ qua trang login và logout
            if (path.equals("/staff/login") || path.equals("/staff/logout")) {
                return true;
            }
            String token = jwtCookieHelper.extractCookieToken(request, "staff_token");
            if (token == null || !jwtCookieHelper.isValidRoleToken(token, "STAFF")) {
                response.sendRedirect("/staff/login");
                return false;
            }
            return true;
        }

        return true;
    }
}
