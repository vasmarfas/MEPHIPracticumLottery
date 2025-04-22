package org.mephi_kotlin_band.lottery.features.user.mapper;

import org.mephi_kotlin_band.lottery.features.user.dto.ReferralDto;
import org.mephi_kotlin_band.lottery.features.user.model.Referral;

public class ReferralMapper {

    public static ReferralDto toDto(Referral referral) {
        return ReferralDto.builder()
                .id(referral.getId())
                .referrerId(referral.getReferrer().getId())
                .referrerUsername(referral.getReferrer().getUsername())
                .referredId(referral.getReferred().getId())
                .referredUsername(referral.getReferred().getUsername())
                .createdAt(referral.getCreatedAt())
                .bonusRewarded(referral.isBonusRewarded())
                .bonusAmount(referral.getBonusAmount())
                .build();
    }
}