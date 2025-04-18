package org.mephi_kotlin_band.lottery.features.lottery.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDrawRequest {
    @NotNull(message = "Lottery type is required")
    private String lotteryType;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    public Draw.LotteryType getLotteryTypeEnum() {
        return Draw.LotteryType.valueOf(lotteryType);
    }
} 