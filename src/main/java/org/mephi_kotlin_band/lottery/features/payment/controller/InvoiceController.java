package org.mephi_kotlin_band.lottery.features.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mephi_kotlin_band.lottery.features.payment.dto.CreateInvoiceRequest;
import org.mephi_kotlin_band.lottery.features.payment.dto.InvoiceDto;
import org.mephi_kotlin_band.lottery.features.payment.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/invoice")
    public ResponseEntity<InvoiceDto> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(request));
    }

    @GetMapping("/invoice/{id}")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }
}