package com.ev.batteryswap.services.interfaces;

import com.ev.batteryswap.dto.UserProfileDTO;

import java.util.Optional;

public interface IUserService {
    Optional<UserProfileDTO> findById(int id);
    Optional<UserProfileDTO> findByUsername(String username);
}
