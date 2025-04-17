package org.mephi_kotlin_band.lottery.features.lottery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mephi_kotlin_band.lottery.features.lottery.dto.CreateTicketRequest;
import org.mephi_kotlin_band.lottery.features.lottery.dto.TicketDto;
import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;
import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;
import org.mephi_kotlin_band.lottery.features.lottery.repository.DrawRepository;
import org.mephi_kotlin_band.lottery.features.lottery.repository.TicketRepository;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final DrawRepository drawRepository;

    @Override
    @Transactional
    public TicketDto createTicket(CreateTicketRequest request, User user) {
        Draw draw = drawRepository.findById(request.getDrawId())
                .orElseThrow(() -> new IllegalArgumentException("Draw not found with id: " + request.getDrawId()));

        if (draw.getStatus() != Draw.Status.ACTIVE) {
            throw new IllegalStateException("Draw is not active");
        }

        if (!validateTicketNumbers(request.getNumbers(), draw.getLotteryType())) {
            throw new IllegalArgumentException("Invalid numbers format for lottery type: " + draw.getLotteryType());
        }

        Ticket ticket = Ticket.builder()
                .user(user)
                .draw(draw)
                .numbers(request.getNumbers())
                .status(Ticket.Status.PENDING)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Created new ticket: {} for user: {}", savedTicket, user.getUsername());
        return TicketDto.fromEntity(savedTicket);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDto getTicketById(UUID id, User user) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with id: " + id));

        if (!ticket.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new IllegalArgumentException("User does not have access to this ticket");
        }

        return TicketDto.fromEntity(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> getUserTickets(User user) {
        List<Ticket> tickets = ticketRepository.findByUser(user);
        return tickets.stream()
                .map(TicketDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> getUserTicketsForDraw(UUID drawId, User user) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("Draw not found with id: " + drawId));

        List<Ticket> tickets = ticketRepository.findByUserAndDraw(user, draw);
        return tickets.stream()
                .map(TicketDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateTicketNumbers(String numbers, Draw.LotteryType lotteryType) {
        try {
            String[] numberArr = numbers.split(",");
            Set<Integer> numberSet = Arrays.stream(numberArr)
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());

            switch (lotteryType) {
                case FIVE_OUT_OF_36:
                    return numberSet.size() == 5 && numberSet.stream().allMatch(n -> n >= 1 && n <= 36);
                case SIX_OUT_OF_45:
                    return numberSet.size() == 6 && numberSet.stream().allMatch(n -> n >= 1 && n <= 45);
                case SEVEN_OUT_OF_49:
                    return numberSet.size() == 7 && numberSet.stream().allMatch(n -> n >= 1 && n <= 49);
                default:
                    return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }
} 