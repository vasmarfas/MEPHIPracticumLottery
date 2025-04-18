package org.mephi_kotlin_band.lottery.features.lottery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mephi_kotlin_band.lottery.features.lottery.dto.CreateDrawRequest;
import org.mephi_kotlin_band.lottery.features.lottery.dto.DrawDto;
import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;
import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;
import org.mephi_kotlin_band.lottery.features.lottery.repository.DrawRepository;
import org.mephi_kotlin_band.lottery.features.lottery.repository.TicketRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DrawServiceImpl implements DrawService {

    private final DrawRepository drawRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public DrawDto createDraw(CreateDrawRequest request) {
        Draw draw = Draw.builder()
                .lotteryType(request.getLotteryTypeEnum())
                .startTime(request.getStartTime())
                .status(Draw.Status.PLANNED)
                .build();

        Draw savedDraw = drawRepository.save(draw);
        log.info("Created new draw: {}", savedDraw);
        return DrawDto.fromEntity(savedDraw);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DrawDto> getAllDraws() {
        return drawRepository.findAll().stream()
                .map(DrawDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DrawDto> getActiveDraws() {
        return drawRepository.findByStatus(Draw.Status.ACTIVE).stream()
                .map(DrawDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DrawDto getDrawById(UUID id) {
        Draw draw = drawRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Draw not found with id: " + id));
        return DrawDto.fromEntity(draw);
    }

    @Override
    @Transactional
    public DrawDto cancelDraw(UUID id) {
        Draw draw = drawRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Draw not found with id: " + id));

        if (draw.getStatus() == Draw.Status.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed draw");
        }

        draw.setStatus(Draw.Status.CANCELLED);
        Draw savedDraw = drawRepository.save(draw);

        // Invalidate all tickets for this draw
        List<Ticket> tickets = ticketRepository.findByDraw(draw);
        tickets.forEach(ticket -> ticket.setStatus(Ticket.Status.INVALID));
        ticketRepository.saveAll(tickets);

        log.info("Cancelled draw: {}", savedDraw);
        return DrawDto.fromEntity(savedDraw);
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 60000) // Run every minute
    public void updateDrawStatuses() {
        LocalDateTime now = LocalDateTime.now();

        // Update PLANNED to ACTIVE
        List<Draw> plannedDraws = drawRepository.findByStartTimeBeforeAndStatus(now, Draw.Status.PLANNED);
        plannedDraws.forEach(draw -> {
            draw.setStatus(Draw.Status.ACTIVE);
            log.info("Updated draw status from PLANNED to ACTIVE: {}", draw);
        });
        drawRepository.saveAll(plannedDraws);

        // Schedule completed draws to be processed by ResultService
        List<Draw> activeDraws = drawRepository.findByStatus(Draw.Status.ACTIVE);
        activeDraws.forEach(draw -> {
            if (draw.getStartTime().plusHours(1).isBefore(now)) { // Assuming a draw lasts for 1 hour
                draw.setStatus(Draw.Status.COMPLETED);
                log.info("Updated draw status from ACTIVE to COMPLETED: {}", draw);
            }
        });
        drawRepository.saveAll(activeDraws);
    }
} 