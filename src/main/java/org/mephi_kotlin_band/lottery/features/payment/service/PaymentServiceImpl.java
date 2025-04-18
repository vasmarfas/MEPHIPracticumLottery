package org.mephi_kotlin_band.lottery.features.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mephi_kotlin_band.lottery.features.lottery.dto.CreateTicketRequest;
import org.mephi_kotlin_band.lottery.features.lottery.service.TicketService;
import org.mephi_kotlin_band.lottery.features.payment.dto.CreatePaymentRequest;
import org.mephi_kotlin_band.lottery.features.payment.dto.PaymentDto;
import org.mephi_kotlin_band.lottery.features.payment.model.Invoice;
import org.mephi_kotlin_band.lottery.features.payment.model.Payment;
import org.mephi_kotlin_band.lottery.features.payment.repository.InvoiceRepository;
import org.mephi_kotlin_band.lottery.features.payment.repository.PaymentRepository;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private final TicketService ticketService;
    private final ObjectMapper objectMapper;
    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public PaymentDto processPayment(CreatePaymentRequest request, User user) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + request.getInvoiceId()));

        if (invoice.getStatus() != Invoice.Status.PENDING) {
            throw new IllegalStateException("Invoice is not in PENDING state");
        }

        // Validate payment information (for test environment)
        boolean isSuccessful = validatePaymentInfo(request.getCardNumber(), request.getCvc());

        Payment.Status status = isSuccessful ? Payment.Status.SUCCESS : Payment.Status.FAILED;

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amount(invoice.getAmount())
                .status(status)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Processed payment: {} with status: {}", savedPayment.getId(), status);

        // Update invoice status
        invoiceService.updateInvoiceStatus(
                invoice.getId(),
                isSuccessful ? Invoice.Status.PAID.name() : Invoice.Status.CANCELLED.name()
        );

        // If payment is successful, create the ticket
        if (isSuccessful) {
            try {
                CreateTicketRequest ticketRequest = objectMapper.readValue(
                        invoice.getTicketData(),
                        CreateTicketRequest.class
                );
                
                ticketService.createTicket(ticketRequest, user);
                log.info("Created ticket for successful payment: {}", savedPayment.getId());
            } catch (Exception e) {
                log.error("Error creating ticket after payment: {}", e.getMessage(), e);
                throw new RuntimeException("Error creating ticket after payment", e);
            }
        }

        return PaymentDto.fromEntity(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + id));
        return PaymentDto.fromEntity(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByInvoiceId(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found with id: " + invoiceId));

        Payment payment = paymentRepository.findByInvoice(invoice)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for invoice: " + invoiceId));

        return PaymentDto.fromEntity(payment);
    }

    /**
     * Validate payment information for test environment.
     * In a real environment, this would call the payment processor API.
     * 
     * For test purposes:
     * - Any card number is accepted if CVC = 123
     * - 80% success rate, 20% failure rate
     */
    private boolean validatePaymentInfo(String cardNumber, String cvc) {
        // First validation: CVC must be 123 for test environment
        if (!cvc.equals("123")) {
            return false;
        }

        // Second validation: 80% success rate, 20% failure rate
        return random.nextInt(100) < 80;
    }
} 