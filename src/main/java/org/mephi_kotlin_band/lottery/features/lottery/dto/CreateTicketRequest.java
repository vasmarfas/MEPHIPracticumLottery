package org.mephi_kotlin_band.lottery.features.lottery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {
    @NotNull(message = "Draw ID is required")
    private UUID drawId;

    @NotBlank(message = "Ticket numbers are required")
    private String numbers;
} 