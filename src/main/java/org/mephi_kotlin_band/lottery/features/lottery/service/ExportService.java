package org.mephi_kotlin_band.lottery.features.lottery.service;

import java.time.LocalDate;
import java.util.UUID;

public interface ExportService {
    String exportDrawResultsAsCsv(UUID drawId);
    String exportDrawResultsAsJson(UUID drawId);
    String exportMonthlyStatisticsAsCsv(int year, int month);
    String exportMonthlyStatisticsAsJson(int year, int month);
    String exportUserHistoryAsCsv(UUID userId);
    String exportUserHistoryAsJson(UUID userId);
} 