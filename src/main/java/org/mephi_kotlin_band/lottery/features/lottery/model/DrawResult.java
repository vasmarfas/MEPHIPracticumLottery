package org.mephi_kotlin_band.lottery.features.lottery.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "draw_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DrawResult {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "draw_id", nullable = false)
    private Draw draw;

    @Column(nullable = false)
    private String winningCombination;

    @Column(nullable = false)
    private LocalDateTime resultTime;
} 