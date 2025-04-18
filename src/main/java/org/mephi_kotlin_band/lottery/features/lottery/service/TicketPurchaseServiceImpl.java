package org.mephi_kotlin_band.lottery.features.lottery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mephi_kotlin_band.lottery.features.lottery.dto.CreateTicketRequest;
import org.mephi_kotlin_band.lottery.features.lottery.dto.PurchaseTicketRequest;
import org.mephi_kotlin_band.lottery.features.lottery.dto.TicketDto;
import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;
import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;
import org.mephi_kotlin_band.lottery.features.lottery.repository.DrawRepository;
import org.mephi_kotlin_band.lottery.features.lottery.repository.TicketRepository;
import org.mephi_kotlin_band.lottery.features.payment.dto.CreateInvoiceRequest;
import org.mephi_kotlin_band.lottery.features.payment.dto.CreatePaymentRequest;
import org.mephi_kotlin_band.lottery.features.payment.dto.InvoiceDto;
import org.mephi_kotlin_band.lottery.features.payment.model.Invoice;
import org.mephi_kotlin_band.lottery.features.payment.service.InvoiceService;
import org.mephi_kotlin_band.lottery.features.payment.service.PaymentService;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketPurchaseServiceImpl implements TicketPurchaseService {

    private final DrawRepository drawRepository;
    private final TicketRepository ticketRepository;
    private final TicketService ticketService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;
    private final SecureRandom random = new SecureRandom();
    
    // Стоимость билета
    private static final double TICKET_PRICE = 100.0;

    @Override
    @Transactional
    public TicketDto purchaseTicket(PurchaseTicketRequest request, User user) {
        // Проверяем существование тиража и его статус
        Draw draw = drawRepository.findById(request.getDrawId())
                .orElseThrow(() -> new IllegalArgumentException("Draw not found with id: " + request.getDrawId()));

        if (draw.getStatus() != Draw.Status.ACTIVE) {
            throw new IllegalStateException("Draw is not active");
        }

        // Проверяем формат номеров
        if (!validateTicketNumbers(request.getNumbers(), draw.getLotteryType())) {
            throw new IllegalArgumentException("Invalid numbers format for lottery type: " + draw.getLotteryType());
        }

        try {
            // 1. Создаем запрос на создание билета
            CreateTicketRequest ticketRequest = new CreateTicketRequest(
                    request.getDrawId(),
                    request.getNumbers()
            );
            
            // 2. Создаем инвойс
            String ticketData = objectMapper.writeValueAsString(ticketRequest);
            CreateInvoiceRequest invoiceRequest = new CreateInvoiceRequest(
                    ticketData,
                    TICKET_PRICE
            );
            
            InvoiceDto invoice = invoiceService.createInvoice(invoiceRequest);
            
            // 3. Создаем платеж
            CreatePaymentRequest paymentRequest = new CreatePaymentRequest(
                    invoice.getId(),
                    request.getCardNumber(),
                    request.getCvc()
            );
            
            // 4. Обрабатываем платеж и создаем билет
            paymentService.processPayment(paymentRequest, user);
            
            // 5. Получаем созданный билет
            // Поскольку платеж сам создает билет через слушатель событий,
            // нам нужно найти последний созданный билет для этого пользователя и тиража
            Ticket ticket = ticketRepository.findTopByUserAndDrawOrderByCreatedAtDesc(user, draw)
                    .orElseThrow(() -> new IllegalStateException("Failed to create ticket"));
            
            log.info("Purchased ticket in one step: {} for user: {}", ticket.getId(), user.getUsername());
            
            return TicketDto.fromEntity(ticket);
        } catch (Exception e) {
            log.error("Error during ticket purchase: {}", e.getMessage(), e);
            throw new RuntimeException("Error purchasing ticket: " + e.getMessage(), e);
        }
    }
    
    /**
     * Проверка формата номеров билета
     */
    private boolean validateTicketNumbers(String numbers, Draw.LotteryType lotteryType) {
        String[] numbersArray = numbers.split(",");
        
        switch (lotteryType) {
            case FIVE_OUT_OF_36:
                return validateFiveOutOf36(numbersArray);
            case SIX_OUT_OF_45:
                return validateSixOutOf45(numbersArray);
            default:
                return false;
        }
    }
    
    private boolean validateFiveOutOf36(String[] numbers) {
        if (numbers.length != 5) {
            return false;
        }
        
        try {
            for (String number : numbers) {
                int num = Integer.parseInt(number.trim());
                if (num < 1 || num > 36) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean validateSixOutOf45(String[] numbers) {
        if (numbers.length != 6) {
            return false;
        }
        
        try {
            for (String number : numbers) {
                int num = Integer.parseInt(number.trim());
                if (num < 1 || num > 45) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
} 