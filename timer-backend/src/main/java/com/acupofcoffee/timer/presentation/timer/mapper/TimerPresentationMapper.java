package com.acupofcoffee.timer.presentation.timer.mapper;

import com.acupofcoffee.timer.application.timer.StartTimerCommand;
import com.acupofcoffee.timer.application.timer.TimerStateDto;
import com.acupofcoffee.timer.presentation.timer.dto.StartTimerRequest;
import com.acupofcoffee.timer.presentation.timer.dto.TimerStateResponse;
import org.springframework.stereotype.Component;

/**
 * Presentation Layer와 Application Layer 간 데이터 변환을 담당하는 Mapper
 */
@Component
public class TimerPresentationMapper {

    /**
     * StartTimerRequest를 StartTimerCommand로 변환
     */
    public StartTimerCommand toCommand(StartTimerRequest request) {
        return new StartTimerCommand(request.getDurationSeconds());
    }

    /**
     * TimerStateDto를 TimerStateResponse로 변환
     */
    public TimerStateResponse toResponse(TimerStateDto dto) {
        return new TimerStateResponse(
            dto.isRunning(),
            dto.getStartTime(),
            dto.getDurationSeconds(),
            dto.getRemainingSeconds(),
            dto.getCurrentRemainingSeconds()
        );
    }
}