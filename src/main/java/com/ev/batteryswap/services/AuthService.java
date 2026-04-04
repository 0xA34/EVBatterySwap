package com.ev.batteryswap.services;

import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.repositories.UserRepository;
import com.ev.batteryswap.services.interfaces.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public User register(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    @Override
    public boolean login(String username, String password) {
        User user =  userRepository.findByUsername(username);
        if(user == null){
            return false;
        }

        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
