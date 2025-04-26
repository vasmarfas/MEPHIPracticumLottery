package org.mephi_kotlin_band.lottery.features.lottery.controller;

import lombok.RequiredArgsConstructor;
import org.mephi_kotlin_band.lottery.features.lottery.dto.DrawResultDto;
import org.mephi_kotlin_band.lottery.features.lottery.service.DrawResultService;
import org.mephi_kotlin_band.lottery.features.lottery.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DrawResultController {

    private final DrawResultService drawResultService;
    private final ExportService exportService;

    @GetMapping("/draws/{drawId}/results")
    public ResponseEntity<DrawResultDto> getDrawResult(@PathVariable UUID drawId) {
        return ResponseEntity.ok(drawResultService.getResultByDrawId(drawId));
    }

    @PostMapping("/admin/draws/{drawId}/generate-result")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DrawResultDto> generateResult(@PathVariable UUID drawId) {
        return ResponseEntity.ok(drawResultService.generateResult(drawId));
    }

    @GetMapping("/tickets/{ticketId}/check-result")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DrawResultDto> checkTicketResult(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(drawResultService.checkTicketResult(ticketId));
    }

    @GetMapping("/draws/{drawId}/export/csv")
    public ResponseEntity<String> exportDrawResultsCsv(@PathVariable UUID drawId) {
        String csv = exportService.exportDrawResultsAsCsv(drawId);
        String headerValue = String.format("attachment; filename=\"draw-%s-results.csv\"", drawId);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(csv);
    }

    @GetMapping("/draws/{drawId}/export/json")
    public ResponseEntity<String> exportDrawResultsJson(@PathVariable UUID drawId) {
        String json = exportService.exportDrawResultsAsJson(drawId);
        String headerValue = String.format("attachment; filename=\"draw-%s-results.json\"", drawId);


        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(json);
    }

    @GetMapping("/statistics/{year}/{month}/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> exportMonthlyStatisticsCsv(@PathVariable int year, @PathVariable int month) {
        String csv = exportService.exportMonthlyStatisticsAsCsv(year, month);
        String headerValues = String.format("attachment; filename=\"statistics-%d-%02d.csv\"", year, month);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValues)
                .body(csv);
    }

    @GetMapping("/statistics/{year}/{month}/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> exportMonthlyStatisticsJson(@PathVariable int year, @PathVariable int month) {
        String json = exportService.exportMonthlyStatisticsAsJson(year, month);
        String headerValues = String.format("attachment; filename=\"statistics-%d-%02d.json\"", year, month);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValues)
                .body(json);
    }
}
