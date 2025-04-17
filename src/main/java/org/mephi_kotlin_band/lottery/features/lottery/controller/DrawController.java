package org.mephi_kotlin_band.lottery.features.lottery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mephi_kotlin_band.lottery.features.lottery.dto.CreateDrawRequest;
import org.mephi_kotlin_band.lottery.features.lottery.dto.DrawDto;
import org.mephi_kotlin_band.lottery.features.lottery.service.DrawService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DrawController {

    private final DrawService drawService;

    @PostMapping("/admin/draws")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DrawDto> createDraw(@Valid @RequestBody CreateDrawRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(drawService.createDraw(request));
    }

    @GetMapping("/draws")
    public ResponseEntity<List<DrawDto>> getAllDraws() {
        return ResponseEntity.ok(drawService.getAllDraws());
    }

    @GetMapping("/draws/active")
    public ResponseEntity<List<DrawDto>> getActiveDraws() {
        return ResponseEntity.ok(drawService.getActiveDraws());
    }

    @GetMapping("/draws/{id}")
    public ResponseEntity<DrawDto> getDrawById(@PathVariable UUID id) {
        return ResponseEntity.ok(drawService.getDrawById(id));
    }

    @PutMapping("/admin/draws/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DrawDto> cancelDraw(@PathVariable UUID id) {
        return ResponseEntity.ok(drawService.cancelDraw(id));
    }
} 