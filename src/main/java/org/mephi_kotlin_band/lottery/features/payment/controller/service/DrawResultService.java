package org.mephi_kotlin_band.lottery.features.lottery.service;

import org.mephi_kotlin_band.lottery.features.lottery.dto.DrawResultDto;
import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;

import java.util.UUID;

public interface DrawResultService {
    DrawResultDto generateResult(UUID drawId);
    DrawResultDto getResultByDrawId(UUID drawId);
    DrawResultDto checkTicketResult(UUID ticketId);
    void processCompletedDraws();
} 