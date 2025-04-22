package org.mephi_kotlin_band.lottery.features.user.controller;

import lombok.RequiredArgsConstructor;
import org.mephi_kotlin_band.lottery.features.user.dto.ReferralCodeResponse;
import org.mephi_kotlin_band.lottery.features.user.dto.ReferralDto;
import org.mephi_kotlin_band.lottery.features.user.mapper.ReferralMapper;
import org.mephi_kotlin_band.lottery.features.user.model.CustomUserDetails;
import org.mephi_kotlin_band.lottery.features.user.model.Referral;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.mephi_kotlin_band.lottery.features.user.service.ReferralService;
import org.mephi_kotlin_band.lottery.features.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;
    private final UserService userService;

    /**
     * Генерирует реферальный код для текущего пользователя
     * @param currentUser текущий пользователь
     * @return реферальный код
     */
    @GetMapping("/users/me/referral-code")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ReferralCodeResponse> getUserReferralCode(@AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = userService.getUserByUserDetails(currentUser);
        String code = referralService.generateReferralCode(user);
        
        return ResponseEntity.ok(new ReferralCodeResponse(code));
    }

    /**
     * Регистрирует нового пользователя по реферальному коду
     * @param referralCode реферальный код
     * @param userId ID регистрируемого пользователя
     * @return информация о реферале
     */
    @PostMapping("/referrals/register")
    public ResponseEntity<ReferralDto> registerReferral(@RequestParam("code") String referralCode,
                                                       @RequestParam("userId") UUID userId) {
        User referrer = referralService.findUserByReferralCode(referralCode);
        Referral referral = referralService.createReferral(referrer.getId(), userId);
        
        return ResponseEntity.ok(ReferralMapper.toDto(referral));
    }

    /**
     * Получает список рефералов текущего пользователя
     * @param currentUser текущий пользователь
     * @return список рефералов
     */
    @GetMapping("/users/me/referrals")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ReferralDto>> getCurrentUserReferrals(@AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = userService.getUserByUserDetails(currentUser);
        List<Referral> referrals = referralService.getUserReferrals(user.getId());
        
        List<ReferralDto> dtos = referrals.stream()
                .map(ReferralMapper::toDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
} 