package com.acupofcoffee.timer.presentation.timer;

import com.acupofcoffee.timer.application.timer.TimerApplicationService;
import com.acupofcoffee.timer.presentation.timer.dto.StartTimerRequest;
import com.acupofcoffee.timer.presentation.timer.dto.TimerStateResponse;
import com.acupofcoffee.timer.presentation.timer.mapper.TimerPresentationMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Timer REST API Controller
 * TimerApi 인터페이스를 구현하여 Swagger 문서화 및 Mock API 지원
 * Presentation Layer 전용 DTO만 사용
 */
@RestController
@CrossOrigin(origins = "*")
public class TimerController implements TimerApi {

    private final TimerApplicationService timerApplicationService;
    private final TimerPresentationMapper mapper;

    public TimerController(TimerApplicationService timerApplicationService, TimerPresentationMapper mapper) {
        this.timerApplicationService = timerApplicationService;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<TimerStateResponse> getTimerState() {
        var timerStateDto = timerApplicationService.getCurrentTimerState();
        var response = mapper.toResponse(timerStateDto);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TimerStateResponse> startTimer(long durationSeconds) {
        var request = new StartTimerRequest(durationSeconds);
        request.validate(); // Presentation Layer에서 검증

        var command = mapper.toCommand(request);
        var timerStateDto = timerApplicationService.startTimer(command);
        var response = mapper.toResponse(timerStateDto);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TimerStateResponse> pauseTimer() {
        var timerStateDto = timerApplicationService.pauseTimer();
        var response = mapper.toResponse(timerStateDto);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TimerStateResponse> resumeTimer() {
        var timerStateDto = timerApplicationService.resumeTimer();
        var response = mapper.toResponse(timerStateDto);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TimerStateResponse> resetTimer() {
        var timerStateDto = timerApplicationService.resetTimer();
        var response = mapper.toResponse(timerStateDto);
        return ResponseEntity.ok(response);
    }
}