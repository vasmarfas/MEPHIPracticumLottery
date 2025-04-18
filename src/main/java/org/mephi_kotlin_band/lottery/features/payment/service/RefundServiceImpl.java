package org.mephi_kotlin_band.lottery.features.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mephi_kotlin_band.lottery.core.notification.NotificationService;
import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;
import org.mephi_kotlin_band.lottery.features.lottery.repository.TicketRepository;
import org.mephi_kotlin_band.lottery.features.payment.model.Invoice;
import org.mephi_kotlin_band.lottery.features.payment.model.Payment;
import org.mephi_kotlin_band.lottery.features.payment.repository.InvoiceRepository;
import org.mephi_kotlin_band.lottery.features.payment.repository.PaymentRepository;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.mephi_kotlin_band.lottery.features.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundServiceImpl implements RefundService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Payment refundTicket(Ticket ticket, User user) {
        // Проверка, что билет принадлежит указанному пользователю
        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ticket doesn't belong to the user");
        }

        // Проверка, что возврат возможен (тираж отменен или еще не начался)
        if (ticket.getStatus() != Ticket.Status.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Refund is only possible for pending tickets");
        }

        // Найти оригинальные платежи для этого билета
        Optional<Payment> originalPaymentOpt = paymentRepository.findAll().stream()
                .filter(p -> p.getInvoice() != null && 
                       p.getInvoice().getTicketData() != null && 
                       p.getInvoice().getTicketData().contains(ticket.getId().toString()))
                .findFirst();
                
        if (originalPaymentOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Original payment not found");
        }
        
        Payment originalPayment = originalPaymentOpt.get();

        // Создаем инвойс возврата
        Invoice refundInvoice = Invoice.builder()
                .ticketData("Refund for ticket " + ticket.getId())
                .registerTime(LocalDateTime.now())
                .status(Invoice.Status.PENDING)
                .amount(originalPayment.getAmount())
                .build();
        
        Invoice savedInvoice = invoiceRepository.save(refundInvoice);

        // Создание записи о возврате
        Payment refundPayment = Payment.builder()
                .invoice(savedInvoice)
                .amount(-originalPayment.getAmount()) // Отрицательная сумма для возврата
                .status(Payment.Status.SUCCESS)
                .paymentTime(LocalDateTime.now())
                .build();

        // Обновление статуса билета на отмененный
        ticket.setStatus(Ticket.Status.INVALID);
        ticketRepository.save(ticket);

        // Сохранение возврата в БД
        Payment savedRefund = paymentRepository.save(refundPayment);
        
        log.info("Refund processed for ticket {} for user {}", ticket.getId(), user.getId());
        
        try {
            // Обычно здесь был бы вызов реальной платежной системы
            // Для примера просто логируем
            log.info("Sending refund request to payment system for amount: {}", originalPayment.getAmount());
        } catch (Exception e) {
            log.error("Error while processing refund", e);
            notificationService.notifyAdminsAboutError("Failed to process refund", e);
        }

        return savedRefund;
    }

    @Override
    @Transactional
    public Payment refundTicketById(UUID ticketId, UUID userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Ticket not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "User not found"));
        
        return refundTicket(ticket, user);
    }
} 