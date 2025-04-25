package org.mephi_kotlin_band.lottery.features.payment.service;

import org.mephi_kotlin_band.lottery.features.payment.dto.CreateInvoiceRequest;
import org.mephi_kotlin_band.lottery.features.payment.dto.InvoiceDto;

import java.util.UUID;

public interface InvoiceService {
    InvoiceDto createInvoice(CreateInvoiceRequest request);
    InvoiceDto getInvoiceById(UUID id);
    InvoiceDto updateInvoiceStatus(UUID id, String status);
} 