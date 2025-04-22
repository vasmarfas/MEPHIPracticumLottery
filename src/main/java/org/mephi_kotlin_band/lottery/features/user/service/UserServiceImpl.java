package org.mephi_kotlin_band.lottery.features.user.service;

import lombok.RequiredArgsConstructor;
import org.mephi_kotlin_band.lottery.features.user.model.CustomUserDetails;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.mephi_kotlin_band.lottery.features.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: %s".formatted(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: %s".formatted(username)));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUserDetails(CustomUserDetails userDetails) {
        return getUserById(userDetails.getId());
    }
} 