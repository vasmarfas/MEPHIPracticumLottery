package org.mephi_kotlin_band.lottery.features.lottery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mephi_kotlin_band.lottery.features.lottery.dto.CreateTicketRequest;
import org.mephi_kotlin_band.lottery.features.lottery.dto.TicketDto;
import org.mephi_kotlin_band.lottery.features.lottery.service.ExportService;
import org.mephi_kotlin_band.lottery.features.lottery.service.TicketService;
import org.mephi_kotlin_band.lottery.features.user.model.CustomUserDetails;
import org.mephi_kotlin_band.lottery.features.user.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final ExportService exportService;
    private final UserService userService;

    @PostMapping("/tickets")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TicketDto> createTicket(@Valid @RequestBody CreateTicketRequest request, @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ticketService.createTicket(request, userService.getUserByUserDetails(currentUser))
        );
    }

    @GetMapping("/tickets/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TicketDto> getTicketById(@PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(ticketService.getTicketById(id, userService.getUserByUserDetails(currentUser)));
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TicketDto>> getUserTickets(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(ticketService.getUserTickets(userService.getUserByUserDetails(currentUser)));
    }

    @GetMapping("/draws/{drawId}/tickets")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TicketDto>> getUserTicketsForDraw(@PathVariable UUID drawId, @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(ticketService.getUserTicketsForDraw(drawId, userService.getUserByUserDetails(currentUser)));
    }

    @GetMapping("/users/{userId}/history/csv")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<String> exportUserHistoryCsv(@PathVariable UUID userId) {
        String csv = exportService.exportUserHistoryAsCsv(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"user-" + userId + "-history.csv\"")
                .body(csv);
    }

    @GetMapping("/users/{userId}/history/json")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<String> exportUserHistoryJson(@PathVariable UUID userId) {
        String json = exportService.exportUserHistoryAsJson(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"user-" + userId + "-history.json\"")
                .body(json);
    }

    @GetMapping("/users/me/history")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TicketDto>> getCurrentUserHistory(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(ticketService.getUserTickets(userService.getUserByUserDetails(currentUser)));
    }
}

