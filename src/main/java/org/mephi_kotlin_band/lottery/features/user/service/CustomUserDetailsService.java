package org.mephi_kotlin_band.lottery.features.user.service;

import lombok.RequiredArgsConstructor;
import org.mephi_kotlin_band.lottery.features.user.repository.UserRepository;
import org.mephi_kotlin_band.lottery.features.user.model.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: %s".formatted(username)));
    }
}