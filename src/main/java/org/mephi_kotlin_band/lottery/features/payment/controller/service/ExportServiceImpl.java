package org.mephi_kotlin_band.lottery.features.lottery.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;
import org.mephi_kotlin_band.lottery.features.lottery.model.DrawResult;
import org.mephi_kotlin_band.lottery.features.lottery.model.Ticket;
import org.mephi_kotlin_band.lottery.features.lottery.repository.DrawRepository;
import org.mephi_kotlin_band.lottery.features.lottery.repository.DrawResultRepository;
import org.mephi_kotlin_band.lottery.features.lottery.repository.TicketRepository;
import org.mephi_kotlin_band.lottery.features.user.model.User;
import org.mephi_kotlin_band.lottery.features.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportServiceImpl implements ExportService {

    private final DrawRepository drawRepository;
    private final DrawResultRepository resultRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(readOnly = true)
    public String exportDrawResultsAsCsv(UUID drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("Draw not found with id: " + drawId));
                
        if (draw.getStatus() != Draw.Status.COMPLETED) {
            throw new IllegalStateException("Draw is not completed yet");
        }
        
        DrawResult result = resultRepository.findByDraw(draw)
                .orElseThrow(() -> new IllegalStateException("No result found for draw: " + drawId));
                
        List<Ticket> winningTickets = ticketRepository.findByDrawAndStatus(draw, Ticket.Status.WIN);
        
        StringBuilder csv = new StringBuilder();
        csv.append("Draw ID,Lottery Type,Start Time,Winning Combination,Result Time\n");
        csv.append(String.format("%s,%s,%s,%s,%s\n", 
                draw.getId(), 
                draw.getLotteryType(),
                draw.getStartTime().format(DATETIME_FORMATTER),
                result.getWinningCombination(),
                result.getResultTime().format(DATETIME_FORMATTER)
        ));
        
        csv.append("\nWinning Tickets\n");
        csv.append("Ticket ID,User ID,Username,Numbers,Status\n");
        
        for (Ticket ticket : winningTickets) {
            csv.append(String.format("%s,%s,%s,%s,%s\n",
                    ticket.getId(),
                    ticket.getUser().getId(),
                    ticket.getUser().getUsername(),
                    ticket.getNumbers(),
                    ticket.getStatus()
            ));
        }
        
        log.info("Exported CSV results for draw: {}", drawId);
        return csv.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public String exportDrawResultsAsJson(UUID drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("Draw not found with id: " + drawId));
                
        if (draw.getStatus() != Draw.Status.COMPLETED) {
            throw new IllegalStateException("Draw is not completed yet");
        }
        
        DrawResult result = resultRepository.findByDraw(draw)
                .orElseThrow(() -> new IllegalStateException("No result found for draw: " + drawId));
                
        List<Ticket> winningTickets = ticketRepository.findByDrawAndStatus(draw, Ticket.Status.WIN);
        
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("drawId", draw.getId().toString());
            root.put("lotteryType", draw.getLotteryType().name());
            root.put("startTime", draw.getStartTime().format(DATETIME_FORMATTER));
            root.put("winningCombination", result.getWinningCombination());
            root.put("resultTime", result.getResultTime().format(DATETIME_FORMATTER));
            
            ArrayNode tickets = root.putArray("winningTickets");
            
            for (Ticket ticket : winningTickets) {
                ObjectNode ticketNode = tickets.addObject();
                ticketNode.put("ticketId", ticket.getId().toString());
                ticketNode.put("userId", ticket.getUser().getId().toString());
                ticketNode.put("username", ticket.getUser().getUsername());
                ticketNode.put("numbers", ticket.getNumbers());
                ticketNode.put("status", ticket.getStatus().name());
            }
            
            log.info("Exported JSON results for draw: {}", drawId);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (Exception e) {
            log.error("Error exporting JSON for draw {}: {}", drawId, e.getMessage(), e);
            throw new RuntimeException("Error generating JSON export", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String exportMonthlyStatisticsAsCsv(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        
        List<Draw> draws = drawRepository.findAll().stream()
                .filter(draw -> draw.getStartTime().isAfter(startDate) && draw.getStartTime().isBefore(endDate))
                .filter(draw -> draw.getStatus() == Draw.Status.COMPLETED)
                .collect(Collectors.toList());
        
        Map<Draw.LotteryType, Integer> drawsByType = new HashMap<>();
        Map<Draw.LotteryType, Integer> totalTickets = new HashMap<>();
        Map<Draw.LotteryType, Integer> winningTickets = new HashMap<>();
        
        for (Draw draw : draws) {
            Draw.LotteryType type = draw.getLotteryType();
            drawsByType.put(type, drawsByType.getOrDefault(type, 0) + 1);
            
            List<Ticket> tickets = ticketRepository.findByDraw(draw);
            totalTickets.put(type, totalTickets.getOrDefault(type, 0) + tickets.size());
            
            List<Ticket> winners = tickets.stream()
                    .filter(ticket -> ticket.getStatus() == Ticket.Status.WIN)
                    .collect(Collectors.toList());
            winningTickets.put(type, winningTickets.getOrDefault(type, 0) + winners.size());
        }
        
        StringBuilder csv = new StringBuilder();
        csv.append("Monthly Statistics: ").append(year).append("-").append(String.format("%02d", month)).append("\n\n");
        
        csv.append("Lottery Type,Total Draws,Total Tickets,Winning Tickets,Win Rate\n");
        
        for (Draw.LotteryType type : drawsByType.keySet()) {
            int drawCount = drawsByType.get(type);
            int tickets = totalTickets.getOrDefault(type, 0);
            int winners = winningTickets.getOrDefault(type, 0);
            double winRate = tickets > 0 ? (double) winners / tickets * 100 : 0;
            
            csv.append(String.format("%s,%d,%d,%d,%.2f%%\n",
                    type, drawCount, tickets, winners, winRate));
        }
        
        log.info("Exported monthly CSV statistics for {}-{}", year, month);
        return csv.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public String exportMonthlyStatisticsAsJson(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        
        List<Draw> monthlyDraws = drawRepository.findAll().stream()
                .filter(draw -> draw.getStartTime().isAfter(startDate) && draw.getStartTime().isBefore(endDate))
                .filter(draw -> draw.getStatus() == Draw.Status.COMPLETED)
                .collect(Collectors.toList());
        
        Map<Draw.LotteryType, Integer> drawsByType = new HashMap<>();
        Map<Draw.LotteryType, Integer> totalTickets = new HashMap<>();
        Map<Draw.LotteryType, Integer> winningTickets = new HashMap<>();
        
        for (Draw draw : monthlyDraws) {
            Draw.LotteryType type = draw.getLotteryType();
            drawsByType.put(type, drawsByType.getOrDefault(type, 0) + 1);
            
            List<Ticket> tickets = ticketRepository.findByDraw(draw);
            totalTickets.put(type, totalTickets.getOrDefault(type, 0) + tickets.size());
            
            List<Ticket> winners = tickets.stream()
                    .filter(ticket -> ticket.getStatus() == Ticket.Status.WIN)
                    .collect(Collectors.toList());
            winningTickets.put(type, winningTickets.getOrDefault(type, 0) + winners.size());
        }
        
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("year", year);
            root.put("month", month);
            
            ArrayNode statistics = root.putArray("statistics");
            
            for (Draw.LotteryType type : drawsByType.keySet()) {
                int drawCount = drawsByType.get(type);
                int tickets = totalTickets.getOrDefault(type, 0);
                int winners = winningTickets.getOrDefault(type, 0);
                double winRate = tickets > 0 ? (double) winners / tickets * 100 : 0;
                
                ObjectNode typeNode = statistics.addObject();
                typeNode.put("lotteryType", type.name());
                typeNode.put("totalDraws", drawCount);
                typeNode.put("totalTickets", tickets);
                typeNode.put("winningTickets", winners);
                typeNode.put("winRate", String.format("%.2f%%", winRate));
            }
            
            log.info("Exported monthly JSON statistics for {}-{}", year, month);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (Exception e) {
            log.error("Error exporting monthly JSON statistics for {}-{}: {}", year, month, e.getMessage(), e);
            throw new RuntimeException("Error generating JSON export", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String exportUserHistoryAsCsv(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
                
        List<Ticket> userTickets = ticketRepository.findByUser(user);
        
        StringBuilder csv = new StringBuilder();
        csv.append("User History: ").append(user.getUsername()).append("\n\n");
        
        csv.append("Ticket ID,Draw ID,Lottery Type,Purchase Date,Numbers,Status,Draw Date,Result\n");
        
        for (Ticket ticket : userTickets) {
            Draw draw = ticket.getDraw();
            String result = "N/A";
            
            if (draw.getStatus() == Draw.Status.COMPLETED) {
                DrawResult drawResult = resultRepository.findByDraw(draw).orElse(null);
                if (drawResult != null) {
                    result = drawResult.getWinningCombination();
                }
            }
            
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    ticket.getId(),
                    draw.getId(),
                    draw.getLotteryType(),
                    ticket.getCreatedAt().format(DATETIME_FORMATTER),
                    ticket.getNumbers(),
                    ticket.getStatus(),
                    draw.getStartTime().format(DATETIME_FORMATTER),
                    result
            ));
        }
        
        // Summary statistics
        int totalTickets = userTickets.size();
        long winningTickets = userTickets.stream()
                .filter(ticket -> ticket.getStatus() == Ticket.Status.WIN)
                .count();
        double winRate = totalTickets > 0 ? (double) winningTickets / totalTickets * 100 : 0;
        
        csv.append("\nSummary\n");
        csv.append(String.format("Total Tickets,%d\n", totalTickets));
        csv.append(String.format("Winning Tickets,%d\n", winningTickets));
        csv.append(String.format("Win Rate,%.2f%%\n", winRate));
        
        log.info("Exported CSV user history for user: {}", userId);
        return csv.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public String exportUserHistoryAsJson(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
                
        List<Ticket> userTickets = ticketRepository.findByUser(user);
        
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("userId", user.getId().toString());
            root.put("username", user.getUsername());
            
            ArrayNode tickets = root.putArray("tickets");
            
            for (Ticket ticket : userTickets) {
                Draw draw = ticket.getDraw();
                String result = "N/A";
                
                if (draw.getStatus() == Draw.Status.COMPLETED) {
                    DrawResult drawResult = resultRepository.findByDraw(draw).orElse(null);
                    if (drawResult != null) {
                        result = drawResult.getWinningCombination();
                    }
                }
                
                ObjectNode ticketNode = tickets.addObject();
                ticketNode.put("ticketId", ticket.getId().toString());
                ticketNode.put("drawId", draw.getId().toString());
                ticketNode.put("lotteryType", draw.getLotteryType().name());
                ticketNode.put("purchaseDate", ticket.getCreatedAt().format(DATETIME_FORMATTER));
                ticketNode.put("numbers", ticket.getNumbers());
                ticketNode.put("status", ticket.getStatus().name());
                ticketNode.put("drawDate", draw.getStartTime().format(DATETIME_FORMATTER));
                ticketNode.put("result", result);
            }
            
            // Summary statistics
            int totalTickets = userTickets.size();
            long winningTickets = userTickets.stream()
                    .filter(ticket -> ticket.getStatus() == Ticket.Status.WIN)
                    .count();
            double winRate = totalTickets > 0 ? (double) winningTickets / totalTickets * 100 : 0;
            
            ObjectNode summary = root.putObject("summary");
            summary.put("totalTickets", totalTickets);
            summary.put("winningTickets", (int) winningTickets);
            summary.put("winRate", String.format("%.2f%%", winRate));
            
            log.info("Exported JSON user history for user: {}", userId);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (Exception e) {
            log.error("Error exporting JSON for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error generating JSON export", e);
        }
    }
} 