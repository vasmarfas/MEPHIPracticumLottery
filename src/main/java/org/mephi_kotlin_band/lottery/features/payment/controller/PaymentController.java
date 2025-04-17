package org.mephi_kotlin_band.lottery.features.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mephi_kotlin_band.lottery.features.payment.dto.CreatePaymentRequest;
import org.mephi_kotlin_band.lottery.features.payment.dto.PaymentDto;
import org.mephi_kotlin_band.lottery.features.payment.service.PaymentService;
import org.mephi_kotlin_band.lottery.features.user.model.CustomUserDetails;
import org.mephi_kotlin_band.lottery.features.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    @PostMapping("/payments")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PaymentDto> processPayment(@Valid @RequestBody CreatePaymentRequest request,
                                                     @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.processPayment(request, userService.getUserByUserDetails(currentUser)));
    }

    @GetMapping("/payments/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/payments/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PaymentDto> getPaymentByInvoiceId(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(paymentService.getPaymentByInvoiceId(invoiceId));
    }
} 