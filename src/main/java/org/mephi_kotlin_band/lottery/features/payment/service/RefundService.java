package org.mephi_kotlin_band.lottery.features.payment.service;

import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;
import org.mephi_kotlin_band.lottery.features.payment.model.Payment;
import org.mephi_kotlin_band.lottery.features.user.model.User;

import java.util.UUID;

public interface RefundService {
    
    /**
     * Выполняет возврат средств за билет 
     * @param ticket билет, за который выполняется возврат
     * @param user пользователь, которому выполняется возврат
     * @return Информация о платеже возврата
     */
    Payment refundTicket(Ticket ticket, User user);
    
    /**
     * Выполняет возврат средств за билет по его ID
     * @param ticketId ID билета
     * @param userId ID пользователя
     * @return Информация о платеже возврата
     */
    Payment refundTicketById(UUID ticketId, UUID userId);
} 