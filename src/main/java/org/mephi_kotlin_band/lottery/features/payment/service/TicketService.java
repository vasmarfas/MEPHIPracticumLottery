package org.mephi_kotlin_band.lottery.features.lottery.service;

import org.mephi_kotlin_band.lottery.features.lottery.dto.CreateTicketRequest;
import org.mephi_kotlin_band.lottery.features.lottery.dto.TicketDto;
import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;
import org.mephi_kotlin_band.lottery.features.user.model.User;

import java.util.List;
import java.util.UUID;

public interface TicketService {
    TicketDto createTicket(CreateTicketRequest request, User user);
    TicketDto getTicketById(UUID id, User user);
    List<TicketDto> getUserTickets(User user);
    List<TicketDto> getUserTicketsForDraw(UUID drawId, User user);
    boolean validateTicketNumbers(String numbers, Draw.LotteryType lotteryType);
}

