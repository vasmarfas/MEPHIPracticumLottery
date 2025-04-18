package org.mephi_kotlin_band.lottery.core.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;
import org.mephi_kotlin_band.lottery.features.lottery.model.DrawResult;
import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramNotificationService implements NotificationService {

    private final RestTemplate restTemplate;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.admin.chatId}")
    private String adminChatId;

    private String getTelegramApiUrl() {
        return "https://api.telegram.org/bot" + botToken + "/sendMessage";
    }

    @Override
    public void notifyWinner(User user, Ticket ticket, DrawResult result) {
        // В реальном приложении нужно хранить telegramChatId пользователя
        // Для примера предположим, что это часть User модели или хранится отдельно
        String userChatId = getUserTelegramChatId(user);
        
        if (userChatId == null || userChatId.isEmpty()) {
            log.warn("Cannot send Telegram winner notification: user {} has no Telegram chat ID", user.getId());
            return;
        }

        String message = String.format(
                "🎉 Поздравляем!\n\n" +
                "Ваш билет №%s выиграл в тираже лотереи.\n" +
                "Выигрышная комбинация: %s\n",
                ticket.getId(),
                result.getWinningCombination()
        );

        sendTelegramMessage(userChatId, message);
    }

    @Override
    public void notifyDrawResults(Draw draw, DrawResult result) {
        // В реальном приложении нужно отправлять сообщения всем подписанным пользователям
        // Для примера используем уведомление админа
        String message = String.format(
                "📊 Результаты тиража лотереи\n\n" +
                "Тираж №%s\n" +
                "Тип лотереи: %s\n" +
                "Выигрышная комбинация: %s\n",
                draw.getId(),
                draw.getLotteryType(),
                result.getWinningCombination()
        );

        sendTelegramMessage(adminChatId, message);
    }

    @Override
    public void notifyAdminsAboutError(String errorMessage, Exception exception) {
        String message = String.format(
                "⚠️ КРИТИЧЕСКАЯ ОШИБКА\n\n" +
                "Сообщение: %s\n\n" +
                "Подробности: %s",
                errorMessage,
                exception != null ? exception.getMessage() : "Детали недоступны"
        );

        sendTelegramMessage(adminChatId, message);
    }

    private void sendTelegramMessage(String chatId, String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("chat_id", chatId);
            body.put("text", text);
            body.put("parse_mode", "HTML");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(getTelegramApiUrl(), request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Telegram notification sent to chat ID {}", chatId);
            } else {
                log.error("Failed to send Telegram notification: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("Error sending Telegram notification", e);
        }
    }

    // Это симуляция. В реальном приложении нужно хранить telegramChatId пользователя
    private String getUserTelegramChatId(User user) {
        // В реальном приложении это поле должно быть в модели User или в отдельной таблице
        return null;
    }
} 