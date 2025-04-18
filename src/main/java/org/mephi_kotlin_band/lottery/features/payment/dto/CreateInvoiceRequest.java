package org.mephi_kotlin_band.lottery.features.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {
    @NotBlank(message = "Ticket data is required")
    private String ticketData;

    @Positive(message = "Amount must be positive")
    private double amount;
} 