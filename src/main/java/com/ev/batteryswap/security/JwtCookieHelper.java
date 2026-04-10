package com.ev.batteryswap.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieHelper {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtCookieHelper(
        JwtTokenProvider jwtTokenProvider,
        UserDetailsService userDetailsService,
        TokenBlacklistService tokenBlacklistService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public String generateRoleToken(
        UserDetails userDetails,
        String role,
        String userName
    ) {
        return jwtTokenProvider.generateToken(
            Map.of("role", role, "username", userName),
            userDetails
        );
    }

    public String extractCookieToken(
        HttpServletRequest request,
        String cookieName
    ) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
            .filter(c -> cookieName.equals(c.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
    }

    public boolean isValidRoleToken(String token, String requiredRole) {
        try {
            if (tokenBlacklistService.isBlacklisted(token)) return false;
            String role = jwtTokenProvider.extractRole(token);
            if (!requiredRole.equals(role)) return false;
            String username = jwtTokenProvider.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(
                username
            );
            return jwtTokenProvider.isTokenValid(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }

    public void setTokenCookie(
        HttpServletResponse response,
        String cookieName,
        String token,
        String path
    ) {
        Instant expiration = jwtTokenProvider.getExpiration(token);
        long maxAge = Duration.between(Instant.now(), expiration).getSeconds();

        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
            .httpOnly(true)
            .path(path)
            .maxAge(maxAge)
            .sameSite("Strict")
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void expireCookie(
        HttpServletResponse response,
        String cookieName,
        String path
    ) {
        ResponseCookie expired = ResponseCookie.from(cookieName, "")
            .httpOnly(true)
            .path(path)
            .maxAge(0)
            .sameSite("Strict")
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expired.toString());
    }

    public void revokeAndExpireCookie(
        HttpServletRequest request,
        HttpServletResponse response,
        String cookieName,
        String path
    ) {
        String token = extractCookieToken(request, cookieName);
        if (token != null) {
            try {
                Instant expiration = jwtTokenProvider.getExpiration(token);
                tokenBlacklistService.blacklist(token, expiration);
            } catch (Exception ignored) {}
        }
        expireCookie(response, cookieName, path);
    }
}
