package org.mephi_kotlin_band.lottery.features.payment.controller;

import lombok.RequiredArgsConstructor;
import org.mephi_kotlin_band.lottery.features.payment.dto.PaymentDto;
import org.mephi_kotlin_band.lottery.features.payment.model.Payment;
import org.mephi_kotlin_band.lottery.features.payment.service.RefundService;
import org.mephi_kotlin_band.lottery.features.user.model.CustomUserDetails;
import org.mephi_kotlin_band.lottery.features.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;
    private final UserService userService;

    /**
     * Запрос на возврат средств за билет
     * @param ticketId ID билета
     * @param currentUser текущий пользователь
     * @return информация о возврате
     */
    @PostMapping("/tickets/{ticketId}/refund")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PaymentDto> refundTicket(@PathVariable UUID ticketId,
                                                 @AuthenticationPrincipal CustomUserDetails currentUser) {
        Payment refund = refundService.refundTicketById(ticketId, 
                userService.getUserByUserDetails(currentUser).getId());
        
        // Преобразуем Payment в PaymentDto (предполагаем, что такой метод существует)
        // Здесь упрощенный вариант создания DTO
        PaymentDto refundDto = PaymentDto.builder()
                .id(refund.getId())
                .amount(refund.getAmount())
                .status(refund.getStatus().name())
                .paymentTime(refund.getPaymentTime())
                .build();
        
        return ResponseEntity.ok(refundDto);
    }
} 