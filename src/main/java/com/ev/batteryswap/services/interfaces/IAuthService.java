package com.ev.batteryswap.services.interfaces;

import com.ev.batteryswap.dto.AuthRequestLogin;
import com.ev.batteryswap.dto.AuthRequestRegister;
import com.ev.batteryswap.dto.AuthResponse;

public interface IAuthService {
    AuthResponse register(AuthRequestRegister request);
    AuthResponse login(AuthRequestLogin request);
    void logout(String token);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
