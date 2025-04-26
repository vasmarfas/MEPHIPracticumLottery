package org.mephi_kotlin_band.lottery.features.lottery.model;

import jakarta.persistence.*;
import lombok.*;
import org.mephi_kotlin_band.lottery.features.user.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Ticket {

    public enum Status {
        PENDING,
        WIN,
        LOSE,
        INVALID
    }

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "draw_id", nullable = false)
    private Draw draw;

    @Column(nullable = false)
    private String numbers;

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

