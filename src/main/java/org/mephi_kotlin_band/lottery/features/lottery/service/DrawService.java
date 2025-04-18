package org.mephi_kotlin_band.lottery.features.lottery.service;

import org.mephi_kotlin_band.lottery.features.lottery.dto.CreateDrawRequest;
import org.mephi_kotlin_band.lottery.features.lottery.dto.DrawDto;
import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;

import java.util.List;
import java.util.UUID;

public interface DrawService {
    DrawDto createDraw(CreateDrawRequest request);
    List<DrawDto> getAllDraws();
    List<DrawDto> getActiveDraws();
    DrawDto getDrawById(UUID id);
    DrawDto cancelDraw(UUID id);
    void updateDrawStatuses();
} 