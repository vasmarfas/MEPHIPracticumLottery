package org.mephi_kotlin_band.lottery.features.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mephi_kotlin_band.lottery.features.payment.dto.CreateInvoiceRequest;
import org.mephi_kotlin_band.lottery.features.payment.dto.InvoiceDto;
import org.mephi_kotlin_band.lottery.features.payment.model.Invoice;
import org.mephi_kotlin_band.lottery.features.payment.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public InvoiceDto createInvoice(CreateInvoiceRequest request) {
        Invoice invoice = Invoice.builder()
                .ticketData(request.getTicketData())
                .amount(request.getAmount())
                .status(Invoice.Status.PENDING)
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Created new invoice: {}", savedInvoice);
        return InvoiceDto.fromEntity(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto getInvoiceById(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + id));
        return InvoiceDto.fromEntity(invoice);
    }

    @Override
    @Transactional
    public InvoiceDto updateInvoiceStatus(UUID id, String status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + id));

        Invoice.Status newStatus;
        try {
            newStatus = Invoice.Status.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        invoice.setStatus(newStatus);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Updated invoice status: {} to {}", id, status);
        return InvoiceDto.fromEntity(savedInvoice);
    }
} 