package org.mephi_kotlin_band.lottery.features.payment.repository;

import org.mephi_kotlin_band.lottery.features.payment.model.Invoice;
import org.mephi_kotlin_band.lottery.features.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByInvoice(Invoice invoice);
} 