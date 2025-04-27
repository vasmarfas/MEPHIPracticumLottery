package org.mephi_kotlin_band.lottery.features.lottery.service;

import org.mephi_kotlin_band.lottery.features.lottery.dto.PurchaseTicketRequest;
import org.mephi_kotlin_band.lottery.features.lottery.dto.TicketDto;
import org.mephi_kotlin_band.lottery.features.user.model.User;

public interface TicketPurchaseService {
    TicketDto purchaseTicket(PurchaseTicketRequest request, User user);
} 