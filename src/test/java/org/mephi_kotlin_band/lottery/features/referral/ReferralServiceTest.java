package org.mephi_kotlin_band.lottery.features.referral;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mephi_kotlin_band.lottery.features.user.model.Referral;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.mephi_kotlin_band.lottery.features.user.repository.ReferralRepository;
import org.mephi_kotlin_band.lottery.features.user.repository.UserRepository;
import org.mephi_kotlin_band.lottery.features.user.service.ReferralServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReferralServiceTest {

    @Mock
    private ReferralRepository referralRepository;
    
    @Mock
    private UserRepository userRepository;
    
    private ReferralServiceImpl referralService;
    
    @BeforeEach
    void setUp() {
        referralService = new ReferralServiceImpl(referralRepository, userRepository);
    }
    
    @Test
    void createReferral_shouldCreateNewReferral_whenValidInput() {
        // Arrange
        UUID referrerId = UUID.randomUUID();
        UUID referredId = UUID.randomUUID();
        
        User referrer = User.builder()
                .id(referrerId)
                .username("referrer")
                .build();
        
        User referred = User.builder()
                .id(referredId)
                .username("referred")
                .build();
        
        Referral newReferral = Referral.builder()
                .id(UUID.randomUUID())
                .referrer(referrer)
                .referred(referred)
                .bonusRewarded(false)
                .build();
        
        when(userRepository.findById(referrerId)).thenReturn(Optional.of(referrer));
        when(userRepository.findById(referredId)).thenReturn(Optional.of(referred));
        when(referralRepository.findByReferred(referred)).thenReturn(null);
        when(referralRepository.save(any(Referral.class))).thenReturn(newReferral);
        
        // Act
        Referral result = referralService.createReferral(referrerId, referredId);
        
        // Assert
        assertNotNull(result);
        assertEquals(referrer, result.getReferrer());
        assertEquals(referred, result.getReferred());
        assertFalse(result.isBonusRewarded());
        verify(referralRepository, times(1)).save(any(Referral.class));
    }
    
    @Test
    void createReferral_shouldThrowException_whenReferrerNotFound() {
        // Arrange
        UUID referrerId = UUID.randomUUID();
        UUID referredId = UUID.randomUUID();
        
        when(userRepository.findById(referrerId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            referralService.createReferral(referrerId, referredId);
        });
        
        verify(referralRepository, never()).save(any(Referral.class));
    }
    
    @Test
    void createReferral_shouldThrowException_whenReferredNotFound() {
        // Arrange
        UUID referrerId = UUID.randomUUID();
        UUID referredId = UUID.randomUUID();
        
        User referrer = User.builder()
                .id(referrerId)
                .username("referrer")
                .build();
        
        when(userRepository.findById(referrerId)).thenReturn(Optional.of(referrer));
        when(userRepository.findById(referredId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            referralService.createReferral(referrerId, referredId);
        });
        
        verify(referralRepository, never()).save(any(Referral.class));
    }
    
    @Test
    void createReferral_shouldThrowException_whenSelfReferral() {
        // Arrange
        UUID userId = UUID.randomUUID();
        
        User user = User.builder()
                .id(userId)
                .username("user")
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            referralService.createReferral(userId, userId);
        });
        
        verify(referralRepository, never()).save(any(Referral.class));
    }
} 