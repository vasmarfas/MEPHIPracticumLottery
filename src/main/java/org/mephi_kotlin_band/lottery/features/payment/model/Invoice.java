package org.mephi_kotlin_band.lottery.features.payment.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Invoice {

    public enum Status {
        PENDING,
        PAID,
        CANCELLED
    }

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "ticket_data", nullable = false, columnDefinition = "TEXT")
    private String ticketData;

    @Column(name = "register_time", nullable = false)
    private LocalDateTime registerTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private double amount;

    @PrePersist
    protected void onCreate() {
        registerTime = LocalDateTime.now();
        status = Status.PENDING;
    }
} 