package org.mephi_kotlin_band.lottery.features.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mephi_kotlin_band.lottery.features.user.model.Referral;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.mephi_kotlin_band.lottery.features.user.repository.ReferralRepository;
import org.mephi_kotlin_band.lottery.features.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReferralServiceImpl implements ReferralService {
    
    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;
    
    @Value("${lottery.referral.bonus.amount:100.0}")
    private double defaultBonusAmount;
    
    @Override
    @Transactional
    public Referral createReferral(UUID referrerId, UUID referredId) {
        // Проверка, что пользователи существуют
        User referrer = userRepository.findById(referrerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referrer not found"));
        
        User referred = userRepository.findById(referredId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referred user not found"));
        
        // Проверка, что пользователь не приглашает сам себя
        if (referrerId.equals(referredId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot refer themselves");
        }
        
        // Проверка, что пользователь еще не был приглашен
        if (referralRepository.findByReferred(referred) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already has a referrer");
        }
        
        // Создание реферала
        Referral referral = Referral.builder()
                .referrer(referrer)
                .referred(referred)
                .bonusAmount(defaultBonusAmount)
                .bonusRewarded(false)
                .build();
        
        return referralRepository.save(referral);
    }
    
    @Override
    @Transactional
    public Referral awardBonus(Referral referral, double bonusAmount) {
        // Проверка, что бонус еще не был начислен
        if (referral.isBonusRewarded()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bonus already awarded");
        }
        
        // Начисление бонуса
        referral.setBonusAmount(bonusAmount);
        referral.setBonusRewarded(true);
        
        // В реальном приложении здесь было бы начисление бонуса на счет пользователя
        log.info("Awarded bonus of {} to user {}", bonusAmount, referral.getReferrer().getId());
        
        return referralRepository.save(referral);
    }
    
    @Override
    public List<Referral> getUserReferrals(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        return referralRepository.findByReferrer(user);
    }
    
    @Override
    public long countUserReferrals(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        return referralRepository.countByReferrer(user);
    }
    
    @Override
    public String generateReferralCode(User user) {
        // Простая реализация - кодируем ID пользователя в Base64
        String rawCode = user.getId().toString() + "-" + System.currentTimeMillis();
        return Base64.getUrlEncoder().encodeToString(rawCode.getBytes());
    }
    
    @Override
    public User findUserByReferralCode(String referralCode) {
        try {
            // Декодируем Base64 и извлекаем ID пользователя
            String decoded = new String(Base64.getUrlDecoder().decode(referralCode));
            String userId = decoded.split("-")[0];
            
            return userRepository.findById(UUID.fromString(userId))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid referral code");
        }
    }
} 