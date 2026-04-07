package com.ev.batteryswap.services;

import com.ev.batteryswap.dto.AuthRequestLogin;
import com.ev.batteryswap.dto.AuthRequestRegister;
import com.ev.batteryswap.dto.AuthResponse;
import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.repositories.UserRepository;
import com.ev.batteryswap.security.JwtTokenProvider;
import com.ev.batteryswap.security.TokenBlacklistService;
import com.ev.batteryswap.services.interfaces.IAuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtTokenProvider jwtTokenProvider,
        AuthenticationManager authenticationManager,
        UserDetailsService userDetailsService,
        TokenBlacklistService tokenBlacklistService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public AuthResponse register(AuthRequestRegister request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(
            user.getUsername()
        );
        String token = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(
            userDetails
        );

        return AuthResponse.builder()
            .success(true)
            .message("Registration successful")
            .token(token)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .userId(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .role(user.getRole())
            .build();
    }

    @Override
    public AuthResponse login(AuthRequestLogin request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        User user = userRepository.findByUsername(request.getUsername());
        UserDetails userDetails = userDetailsService.loadUserByUsername(
            request.getUsername()
        );
        String token = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(
            userDetails
        );

        return AuthResponse.builder()
            .success(true)
            .message("Login successful")
            .token(token)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .userId(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .role(user.getRole())
            .build();
    }

    @Override
    public void logout(String token) {
        // Add token to blacklist until it naturally expires
        tokenBlacklistService.blacklist(
            token,
            jwtTokenProvider.getExpiration(token)
        );
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
