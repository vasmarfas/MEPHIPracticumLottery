package org.mephi_kotlin_band.lottery.features.lottery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private UUID id;
    private UUID userId;
    private UUID drawId;
    private String numbers;
    private String status;
    private LocalDateTime createdAt;

    public static TicketDto fromEntity(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        
        TicketDto.TicketDtoBuilder builder = TicketDto.builder()
                .id(ticket.getId())
                .numbers(ticket.getNumbers())
                .createdAt(ticket.getCreatedAt());
        
        if (ticket.getUser() != null) {
            builder.userId(ticket.getUser().getId());
        }
        
        if (ticket.getDraw() != null) {
            builder.drawId(ticket.getDraw().getId());
        }
        
        if (ticket.getStatus() != null) {
            builder.status(ticket.getStatus().name());
        }
        
        return builder.build();
    }
} 