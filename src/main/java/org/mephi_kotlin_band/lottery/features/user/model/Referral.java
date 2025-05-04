package org.mephi_kotlin_band.lottery.features.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "referrals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Referral {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User referrer;

    @ManyToOne
    @JoinColumn(name = "referred_user_id", nullable = false)
    private User referred;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "bonus_rewarded")
    private boolean bonusRewarded;

    @Column(name = "bonus_amount")
    private double bonusAmount;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        bonusRewarded = false;
    }
} 