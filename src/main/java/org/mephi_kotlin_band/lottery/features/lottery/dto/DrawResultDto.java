package org.mephi_kotlin_band.lottery.features.lottery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mephi_kotlin_band.lottery.features.lottery.model.DrawResult;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrawResultDto {
    private UUID id;
    private UUID drawId;
    private String winningCombination;
    private LocalDateTime resultTime;

    public static DrawResultDto fromEntity(DrawResult drawResult) {
        return DrawResultDto.builder()
                .id(drawResult.getId())
                .drawId(drawResult.getDraw().getId())
                .winningCombination(drawResult.getWinningCombination())
                .resultTime(drawResult.getResultTime())
                .build();
    }
}
