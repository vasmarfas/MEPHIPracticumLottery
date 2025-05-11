package org.mephi_kotlin_band.lottery.features.lottery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketPurchaseResponse {
    private TicketDto ticket;
    private String paymentStatus;
    private String message;
    
    public static TicketPurchaseResponse success(TicketDto ticket) {
        return TicketPurchaseResponse.builder()
                .ticket(ticket)
                .paymentStatus("SUCCESS")
                .message("Ticket purchased successfully")
                .build();
    }
    
    public static TicketPurchaseResponse failed(String message) {
        return TicketPurchaseResponse.builder()
                .paymentStatus("FAILED")
                .message(message)
                .build();
    }
} 