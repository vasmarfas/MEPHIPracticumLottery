package org.mephi_kotlin_band.lottery.features.payment.repository;

import org.mephi_kotlin_band.lottery.features.payment.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    List<Invoice> findByStatus(Invoice.Status status);
} 