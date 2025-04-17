package org.mephi_kotlin_band.lottery.features.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mephi_kotlin_band.lottery.features.payment.model.Invoice;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private UUID id;
    private String ticketData;
    private LocalDateTime registerTime;
    private String status;
    private double amount;

    public static InvoiceDto fromEntity(Invoice invoice) {
        if (invoice == null) {
            return null;
        }
        
        InvoiceDto.InvoiceDtoBuilder builder = InvoiceDto.builder()
                .id(invoice.getId())
                .ticketData(invoice.getTicketData())
                .registerTime(invoice.getRegisterTime())
                .amount(invoice.getAmount());
        
        if (invoice.getStatus() != null) {
            builder.status(invoice.getStatus().name());
        }
        
        return builder.build();
    }
} 