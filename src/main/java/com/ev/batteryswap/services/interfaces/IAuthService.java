package com.ev.batteryswap.services.interfaces;

import com.ev.batteryswap.pojo.User;

public interface IAuthService {
    User register(User user);
    boolean login(String username, String password);
    boolean existsByUsername(String username);
    // boolean existsByEmail(String email);
}
