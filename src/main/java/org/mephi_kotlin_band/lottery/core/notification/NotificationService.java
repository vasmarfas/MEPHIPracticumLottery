package org.mephi_kotlin_band.lottery.core.notification;

import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;
import org.mephi_kotlin_band.lottery.features.lottery.model.DrawResult;
import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;
import org.mephi_kotlin_band.lottery.features.user.model.User;

public interface NotificationService {
    
    /**
     * Отправляет уведомление о выигрыше пользователю
     */
    void notifyWinner(User user, Ticket ticket, DrawResult result);
    
    /**
     * Отправляет уведомление о результатах тиража
     */
    void notifyDrawResults(Draw draw, DrawResult result);
    
    /**
     * Отправляет уведомление администраторам о критической ошибке
     */
    void notifyAdminsAboutError(String errorMessage, Exception exception);
} 