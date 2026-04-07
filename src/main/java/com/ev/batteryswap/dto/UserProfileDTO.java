package com.ev.batteryswap.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
public class UserProfileDTO {
    private final Integer id;
    private final String username;
    private final String fullName;
    private final String email;
    private final String phoneNumber;
    private final BigDecimal walletBalance;
    private final String role;
    private final String status;
    private final Instant createdAt;
}
