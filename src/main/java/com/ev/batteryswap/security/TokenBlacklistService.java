package com.ev.batteryswap.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory token blacklist for JWT logout.
 * Tokens are stored with their expiration time and cleaned up automatically.
 */
@Service
public class TokenBlacklistService {

    // token → expiration time
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    /**
     * Add a token to the blacklist until its natural expiration.
     */
    public void blacklist(String token, Instant expiresAt) {
        blacklist.put(token, expiresAt);
    }

    /**
     * Check if a token has been blacklisted (i.e. user logged out).
     */
    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    /**
     * Scheduled cleanup: remove tokens that have already expired
     * so the in-memory map doesn't grow unboundedly.
     * Runs every hour.
     */
    @Scheduled(fixedRate = 3_600_000)
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        blacklist.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
