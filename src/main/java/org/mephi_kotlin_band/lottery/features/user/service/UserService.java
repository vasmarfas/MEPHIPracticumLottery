package org.mephi_kotlin_band.lottery.features.user.service;

import org.mephi_kotlin_band.lottery.features.user.model.CustomUserDetails;
import org.mephi_kotlin_band.lottery.features.user.model.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
    User getUserByUsername(String username);
    User getUserByUserDetails(CustomUserDetails userDetails);
} 