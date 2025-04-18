package org.mephi_kotlin_band.lottery.features.payment.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Payment {

    public enum Status {
        SUCCESS,
        FAILED
    }

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "payment_time", nullable = false)
    private LocalDateTime paymentTime;

    @PrePersist
    protected void onCreate() {
        paymentTime = LocalDateTime.now();
    }
} 