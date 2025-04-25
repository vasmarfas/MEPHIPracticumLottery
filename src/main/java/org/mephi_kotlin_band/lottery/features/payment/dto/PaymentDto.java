package org.mephi_kotlin_band.lottery.features.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mephi_kotlin_band.lottery.features.payment.model.Payment;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private UUID id;
    private UUID invoiceId;
    private double amount;
    private String status;
    private LocalDateTime paymentTime;

    public static PaymentDto fromEntity(Payment payment) {
        if (payment == null) {
            return null;
        }
        
        PaymentDto.PaymentDtoBuilder builder = PaymentDto.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentTime(payment.getPaymentTime());
        
        if (payment.getInvoice() != null) {
            builder.invoiceId(payment.getInvoice().getId());
        }
        
        if (payment.getStatus() != null) {
            builder.status(payment.getStatus().name());
        }
        
        return builder.build();
    }
} 