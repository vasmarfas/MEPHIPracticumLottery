package org.mephi_kotlin_band.lottery.features.lottery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mephi_kotlin_band.lottery.features.lottery.dto.PurchaseTicketRequest;
import org.mephi_kotlin_band.lottery.features.lottery.dto.TicketDto;
import org.mephi_kotlin_band.lottery.features.lottery.service.TicketPurchaseService;
import org.mephi_kotlin_band.lottery.features.user.model.CustomUserDetails;
import org.mephi_kotlin_band.lottery.features.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "API для работы с билетами лотереи")
public class TicketPurchaseController {

    private final TicketPurchaseService ticketPurchaseService;
    private final UserService userService;

    /**
     * Покупка билета в один шаг
     * @param request запрос на покупку билета
     * @param currentUser текущий пользователь
     * @return информация о купленном билете
     */
    @PostMapping("/tickets/purchase")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Покупка билета", description = "Создает и оплачивает билет в один шаг")
    public ResponseEntity<TicketDto> purchaseTicket(@Valid @RequestBody PurchaseTicketRequest request,
                                                    @AuthenticationPrincipal CustomUserDetails currentUser) {
        TicketDto ticket = ticketPurchaseService.purchaseTicket(request, userService.getUserByUserDetails(currentUser));
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
} 