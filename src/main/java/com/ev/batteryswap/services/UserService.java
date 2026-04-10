package com.ev.batteryswap.services;

import com.ev.batteryswap.dto.UserProfileDTO;
import com.ev.batteryswap.pojo.User;
import com.ev.batteryswap.repositories.UserRepository;
import com.ev.batteryswap.services.interfaces.IUserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

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

    @Override
    public Page<User> filterUsers(String searchKeyword, Pageable pageable) {
        return userRepository.findAll((Specification<User>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String likePattern = "%" + searchKeyword.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Override
    public void updateUserRole(Integer userId, String role) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        // Xóa tất cả các bản ghi liên quan trước khi xóa user
        entityManager.createNativeQuery("DELETE FROM support_tickets WHERE user_id = :userId")
                .setParameter("userId", userId).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM station_reviews WHERE user_id = :userId")
                .setParameter("userId", userId).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM reservations WHERE user_id = :userId")
                .setParameter("userId", userId).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM swap_transactions WHERE user_id = :userId")
                .setParameter("userId", userId).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM rentals WHERE user_id = :userId")
                .setParameter("userId", userId).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM vehicles WHERE user_id = :userId")
                .setParameter("userId", userId).executeUpdate();
        entityManager.createNativeQuery("UPDATE batteries SET user_id = NULL WHERE user_id = :userId")
                .setParameter("userId", userId).executeUpdate();

        userRepository.deleteById(userId);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User findById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
