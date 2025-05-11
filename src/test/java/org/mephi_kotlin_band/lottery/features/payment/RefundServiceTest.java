package org.mephi_kotlin_band.lottery.features.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mephi_kotlin_band.lottery.core.notification.NotificationService;
import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;
import org.mephi_kotlin_band.lottery.features.lottery.repository.TicketRepository;
import org.mephi_kotlin_band.lottery.features.payment.model.Invoice;
import org.mephi_kotlin_band.lottery.features.payment.model.Payment;
import org.mephi_kotlin_band.lottery.features.payment.repository.InvoiceRepository;
import org.mephi_kotlin_band.lottery.features.payment.repository.PaymentRepository;
import org.mephi_kotlin_band.lottery.features.payment.service.RefundServiceImpl;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.mephi_kotlin_band.lottery.features.user.repository.UserRepository;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefundServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private NotificationService notificationService;

    private RefundServiceImpl refundService;

    @BeforeEach
    void setUp() {
        refundService = new RefundServiceImpl(
                ticketRepository,
                userRepository,
                paymentRepository,
                invoiceRepository,
                notificationService
        );
    }

    @Test
    void refundTicket_shouldProcessRefund_whenValidTicket() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("user")
                .build();

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .user(user)
                .status(Ticket.Status.PENDING)
                .build();

        Invoice originalInvoice = Invoice.builder()
                .id(UUID.randomUUID())
                .ticketData("Ticket " + ticketId)
                .status(Invoice.Status.PAID)
                .amount(100.0)
                .build();

        Payment originalPayment = Payment.builder()
                .id(UUID.randomUUID())
                .invoice(originalInvoice)
                .amount(100.0)
                .status(Payment.Status.SUCCESS)
                .paymentTime(LocalDateTime.now().minusDays(1))
                .build();

        Invoice refundInvoice = Invoice.builder()
                .id(UUID.randomUUID())
                .ticketData("Refund for ticket " + ticketId)
                .status(Invoice.Status.PENDING)
                .amount(100.0)
                .build();

        Payment refundPayment = Payment.builder()
                .id(UUID.randomUUID())
                .invoice(refundInvoice)
                .amount(-100.0)
                .status(Payment.Status.SUCCESS)
                .paymentTime(LocalDateTime.now())
                .build();

        when(paymentRepository.findAll()).thenReturn(List.of(originalPayment));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(refundInvoice);
        when(paymentRepository.save(any(Payment.class))).thenReturn(refundPayment);

        // Act
        Payment result = refundService.refundTicket(ticket, user);

        // Assert
        assertNotNull(result);
        assertEquals(-100.0, result.getAmount());
        assertEquals(Payment.Status.SUCCESS, result.getStatus());
        
        verify(ticketRepository, times(1)).save(ticket);
        assertEquals(Ticket.Status.INVALID, ticket.getStatus());
    }

    @Test
    void refundTicket_shouldThrowException_whenTicketDoesNotBelongToUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("user")
                .build();

        User otherUser = User.builder()
                .id(otherUserId)
                .username("otherUser")
                .build();

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .user(otherUser)
                .status(Ticket.Status.PENDING)
                .build();

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            refundService.refundTicket(ticket, user);
        });

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void refundTicket_shouldThrowException_whenTicketNotPending() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("user")
                .build();

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .user(user)
                .status(Ticket.Status.WIN)
                .build();

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            refundService.refundTicket(ticket, user);
        });

        verify(ticketRepository, never()).save(any(Ticket.class));
    }
} 