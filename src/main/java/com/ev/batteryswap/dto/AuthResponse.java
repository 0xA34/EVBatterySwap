package com.ev.batteryswap.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private final boolean success;
    private final String message;
    private final String token;
    private final String refreshToken;
    private final String tokenType;
    private final Integer userId;
    private final String username;
    private final String fullName;
    private final String role;
}
