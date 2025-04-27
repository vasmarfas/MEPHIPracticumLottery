package org.mephi_kotlin_band.lottery.features.lottery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseTicketRequest {
    @NotNull(message = "Draw ID is required")
    private UUID drawId;

    @NotBlank(message = "Ticket numbers are required")
    private String numbers;
    
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;

    @NotBlank(message = "CVC is required")
    @Pattern(regexp = "\\d{3}", message = "CVC must be 3 digits")
    private String cvc;
} 