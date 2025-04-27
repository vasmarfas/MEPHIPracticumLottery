package org.mephi_kotlin_band.lottery.features.lottery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mephi_kotlin_band.lottery.features.lottery.dto.DrawResultDto;
import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;
import org.mephi_kotlin_band.lottery.features.lottery.model.DrawResult;
import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;
import org.mephi_kotlin_band.lottery.features.lottery.repository.DrawRepository;
import org.mephi_kotlin_band.lottery.features.lottery.repository.DrawResultRepository;
import org.mephi_kotlin_band.lottery.features.lottery.repository.TicketRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DrawResultServiceImpl implements DrawResultService {

    private final DrawRepository drawRepository;
    private final DrawResultRepository drawResultRepository;
    private final TicketRepository ticketRepository;
    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public DrawResultDto generateResult(UUID drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("Draw not found with id: " + drawId));

        if (draw.getStatus() != Draw.Status.COMPLETED) {
            throw new IllegalStateException("Cannot generate result for a draw that is not completed");
        }

        Optional<DrawResult> existingResult = drawResultRepository.findByDraw(draw);
        if (existingResult.isPresent()) {
            return DrawResultDto.fromEntity(existingResult.get());
        }

        String winningCombination = generateWinningCombination(draw.getLotteryType());
        DrawResult result = DrawResult.builder()
                .draw(draw)
                .winningCombination(winningCombination)
                .resultTime(LocalDateTime.now())
                .build();

        DrawResult savedResult = drawResultRepository.save(result);

        updateTicketResults(draw, winningCombination);

        log.info("Generated result for draw {}: {}", drawId, winningCombination);
        return DrawResultDto.fromEntity(savedResult);
    }

    private void updateTicketResults(Draw draw, String winningCombination) {
        List<Ticket> tickets = ticketRepository.findByDraw(draw);
        Set<Integer> winningNumbers = parseNumbers(winningCombination);

        tickets.forEach(ticket -> {
            Set<Integer> ticketNumbers = parseNumbers(ticket.getNumbers());
            boolean isWinner = determineWinStatus(ticketNumbers, winningNumbers, draw.getLotteryType());
            ticket.setStatus(isWinner ? Ticket.Status.WIN : Ticket.Status.LOSE);
        });

        ticketRepository.saveAll(tickets);
    }

    private boolean determineWinStatus(Set<Integer> ticketNumbers, Set<Integer> winningNumbers, Draw.LotteryType lotteryType) {
        Set<Integer> intersection = new HashSet<>(ticketNumbers);
        intersection.retainAll(winningNumbers);

        return switch (lotteryType) {
            case FIVE_OUT_OF_36, SIX_OUT_OF_45 -> intersection.size() >= 3;
            case SEVEN_OUT_OF_49 -> intersection.size() >= 4;
            default -> false;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public DrawResultDto getResultByDrawId(UUID drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("Draw not found with id: " + drawId));

        DrawResult result = drawResultRepository.findByDraw(draw)
                .orElseThrow(() -> new IllegalStateException("Result not found for draw: " + drawId));

        return DrawResultDto.fromEntity(result);
    }

    @Override
    @Transactional(readOnly = true)
    public DrawResultDto checkTicketResult(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with id: " + ticketId));

        Draw draw = ticket.getDraw();
        if (draw.getStatus() != Draw.Status.COMPLETED) {
            throw new IllegalStateException("Draw is not completed yet");
        }

        return getResultByDrawId(draw.getId());
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 60000) // Run every minute
    public void processCompletedDraws() {
        List<Draw> completedDraws = drawRepository.findByStatus(Draw.Status.COMPLETED);

        completedDraws.forEach(draw -> {
            try {
                // Check if result already exists
                Optional<DrawResult> existingResult = drawResultRepository.findByDraw(draw);
                if (existingResult.isEmpty()) {
                    generateResult(draw.getId());
                    log.info("Automatically processed result for completed draw: {}", draw.getId());
                }
            } catch (Exception e) {
                log.error("Error processing result for draw {}: {}", draw.getId(), e.getMessage(), e);
            }
        });
    }

    private String generateWinningCombination(Draw.LotteryType lotteryType) {
        int count;
        int maxNumber = switch (lotteryType) {
            case FIVE_OUT_OF_36 -> {
                count = 5;
                yield 36;
            }
            case SIX_OUT_OF_45 -> {
                count = 6;
                yield 45;
            }
            case SEVEN_OUT_OF_49 -> {
                count = 7;
                yield 49;
            }
            default -> throw new IllegalArgumentException("Unknown lottery type: " + lotteryType);
        };

        Set<Integer> numbers = new HashSet<>();
        while (numbers.size() < count) {
            numbers.add(random.nextInt(maxNumber) + 1);
        }

        return numbers.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private Set<Integer> parseNumbers(String numbers) {
        return Arrays.stream(numbers.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }
}
