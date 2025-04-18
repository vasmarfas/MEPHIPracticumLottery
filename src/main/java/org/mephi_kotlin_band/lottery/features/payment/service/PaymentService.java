package org.mephi_kotlin_band.lottery.features.payment.service;

import org.mephi_kotlin_band.lottery.features.payment.dto.CreatePaymentRequest;
import org.mephi_kotlin_band.lottery.features.payment.dto.PaymentDto;
import org.mephi_kotlin_band.lottery.features.user.model.User;

import java.util.UUID;

public interface PaymentService {
    PaymentDto processPayment(CreatePaymentRequest request, User user);
    PaymentDto getPaymentById(UUID id);
    PaymentDto getPaymentByInvoiceId(UUID invoiceId);
} 