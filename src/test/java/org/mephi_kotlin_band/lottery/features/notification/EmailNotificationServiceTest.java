package org.mephi_kotlin_band.lottery.features.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mephi_kotlin_band.lottery.core.notification.EmailNotificationService;
import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;
import org.mephi_kotlin_band.lottery.features.lottery.model.DrawResult;
import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.mephi_kotlin_band.lottery.features.user.repository.UserRepository;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;
    
    @Mock
    private UserRepository userRepository;
    
    private EmailNotificationService notificationService;
    
    @BeforeEach
    void setUp() {
        notificationService = new EmailNotificationService(mailSender, userRepository);
    }
    
    @Test
    void notifyWinner_shouldSendEmail_whenUserHasEmail() {
        // Arrange
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();
        
        Ticket ticket = Ticket.builder()
                .id(UUID.randomUUID())
                .user(user)
                .build();
        
        DrawResult result = DrawResult.builder()
                .id(UUID.randomUUID())
                .winningCombination("1, 2, 3, 4, 5")
                .build();
        
        // Act
        notificationService.notifyWinner(user, ticket, result);
        
        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
    
    @Test
    void notifyWinner_shouldNotSendEmail_whenUserHasNoEmail() {
        // Arrange
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email(null)
                .build();
        
        Ticket ticket = Ticket.builder()
                .id(UUID.randomUUID())
                .user(user)
                .build();
        
        DrawResult result = DrawResult.builder()
                .id(UUID.randomUUID())
                .winningCombination("1, 2, 3, 4, 5")
                .build();
        
        // Act
        notificationService.notifyWinner(user, ticket, result);
        
        // Assert
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
    
    @Test
    void notifyDrawResults_shouldSendEmailToAllUsers() {
        // Arrange
        User user1 = User.builder()
                .id(UUID.randomUUID())
                .username("user1")
                .email("user1@example.com")
                .build();
        
        User user2 = User.builder()
                .id(UUID.randomUUID())
                .username("user2")
                .email("user2@example.com")
                .build();
        
        List<User> users = List.of(user1, user2);
        
        Draw draw = Draw.builder()
                .id(UUID.randomUUID())
                .lotteryType(Draw.LotteryType.FIVE_OUT_OF_36)
                .build();
        
        DrawResult result = DrawResult.builder()
                .id(UUID.randomUUID())
                .winningCombination("1, 2, 3, 4, 5")
                .build();
        
        when(userRepository.findAll()).thenReturn(users);
        
        // Act
        notificationService.notifyDrawResults(draw, result);
        
        // Assert
        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }
} 