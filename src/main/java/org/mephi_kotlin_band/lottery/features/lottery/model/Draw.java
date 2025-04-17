package org.mephi_kotlin_band.lottery.features.lottery.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "draws")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Draw {

    public enum Status {
        PLANNED,
        ACTIVE,
        COMPLETED,
        CANCELLED
    }

    public enum LotteryType {
        FIVE_OUT_OF_36,
        SIX_OUT_OF_45,
        SEVEN_OUT_OF_49
    }

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LotteryType lotteryType;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 