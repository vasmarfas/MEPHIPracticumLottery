package org.mephi_kotlin_band.lottery.features.lottery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mephi_kotlin_band.lottery.features.lottery.model.Draw;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrawDto {
    private UUID id;
    private String lotteryType;
    private LocalDateTime startTime;
    private String status;
    private LocalDateTime createdAt;

    public static DrawDto fromEntity(Draw draw) {
        if (draw == null) {
            return null;
        }
        
        DrawDto.DrawDtoBuilder builder = DrawDto.builder()
                .id(draw.getId())
                .startTime(draw.getStartTime())
                .createdAt(draw.getCreatedAt());
        
        // Безопасно получаем имя enum
        if (draw.getLotteryType() != null) {
            builder.lotteryType(draw.getLotteryType().name());
        }
        
        if (draw.getStatus() != null) {
            builder.status(draw.getStatus().name());
        }
        
        return builder.build();
    }
} 