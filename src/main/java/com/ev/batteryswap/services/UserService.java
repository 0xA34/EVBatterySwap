package com.ev.batteryswap.services;

import com.ev.batteryswap.dto.UserProfileDTO;
import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.repositories.UserRepository;
import com.ev.batteryswap.services.interfaces.IUserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserProfileDTO> findById(int id) {
        return userRepository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<UserProfileDTO> findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(toDTO(user));
    }

    private UserProfileDTO toDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .walletBalance(user.getWalletBalance())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
