package org.mephi_kotlin_band.lottery.core.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;
import org.mephi_kotlin_band.lottery.features.lottery.model.DrawResult;
import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.mephi_kotlin_band.lottery.features.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${lottery.admin.email}")
    private String adminEmail;

    @Override
    public void notifyWinner(User user, Ticket ticket, DrawResult result) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            log.warn("Cannot send winner notification: user {} has no email", user.getId());
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Поздравляем с выигрышем в лотерее!");
        message.setText(String.format(
                "Уважаемый %s,\n\n" +
                "Поздравляем! Ваш билет №%s выиграл в тираже лотереи.\n" +
                "Выигрышная комбинация: %s\n\n" +
                "С уважением,\n" +
                "Команда лотереи",
                user.getUsername(),
                ticket.getId(),
                result.getWinningCombination()
        ));

        try {
            mailSender.send(message);
            log.info("Winner notification email sent to user {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to send winner notification email to user {}", user.getId(), e);
        }
    }

    @Override
    public void notifyDrawResults(Draw draw, DrawResult result) {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                continue;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Результаты тиража лотереи");
            message.setText(String.format(
                    "Уважаемый %s,\n\n" +
                    "Результаты тиража №%s:\n" +
                    "Тип лотереи: %s\n" +
                    "Выигрышная комбинация: %s\n\n" +
                    "Проверьте свои билеты в личном кабинете.\n\n" +
                    "С уважением,\n" +
                    "Команда лотереи",
                    user.getUsername(),
                    draw.getId(),
                    draw.getLotteryType(),
                    result.getWinningCombination()
            ));

            try {
                mailSender.send(message);
                log.info("Draw results notification email sent to user {}", user.getId());
            } catch (Exception e) {
                log.error("Failed to send draw results notification email to user {}", user.getId(), e);
            }
        }
    }

    @Override
    public void notifyAdminsAboutError(String errorMessage, Exception exception) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(adminEmail);
        message.setSubject("КРИТИЧЕСКАЯ ОШИБКА в системе лотереи");
        message.setText(String.format(
                "Внимание! В системе произошла критическая ошибка:\n\n" +
                "Сообщение: %s\n\n" +
                "Стек вызовов:\n%s",
                errorMessage,
                exception != null ? getStackTraceAsString(exception) : "Стек вызовов недоступен"
        ));

        try {
            mailSender.send(message);
            log.info("Admin error notification email sent");
        } catch (Exception e) {
            log.error("Failed to send admin error notification email", e);
        }
    }

    private String getStackTraceAsString(Exception exception) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
} 