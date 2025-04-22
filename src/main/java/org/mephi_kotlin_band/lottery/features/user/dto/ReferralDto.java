package org.mephi_kotlin_band.lottery.features.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferralDto {
    private UUID id;
    private UUID referrerId;
    private String referrerUsername;
    private UUID referredId;
    private String referredUsername;
    private LocalDateTime createdAt;
    private boolean bonusRewarded;
    private double bonusAmount;
} 