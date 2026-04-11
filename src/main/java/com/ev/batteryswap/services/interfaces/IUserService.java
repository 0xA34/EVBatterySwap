package com.ev.batteryswap.services.interfaces;

import com.ev.batteryswap.dto.UserProfileDTO;
import com.ev.batteryswap.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    Optional<UserProfileDTO> findById(int id);
    Optional<UserProfileDTO> findByUsername(String username);

    Page<User> filterUsers(String searchKeyword, Pageable pageable);

    void updateUserRole(Integer userId, String role);

    void deleteUser(Integer userId);

    void saveUser(User user);

    User findById(Integer userId);

    List<User> getStaffByStation(Integer id);

    List<User> getUsersByRole(String driver);
}
