package com.acupofcoffee.timer.presentation.timer;

import com.acupofcoffee.timer.presentation.timer.dto.TimerStateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 타이머 API 인터페이스
 * Swagger 문서 생성 및 Mock API 관리를 위한 인터페이스
 */
@Tag(name = "Timer", description = "타이머 관리 API")
@RequestMapping("/api/timer")
public interface TimerApi {

    @Operation(
        summary = "타이머 상태 조회",
        description = "현재 타이머의 상태를 조회합니다. 타이머가 없으면 새로 생성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    ResponseEntity<TimerStateResponse> getTimerState();

    @Operation(
        summary = "타이머 시작",
        description = "지정된 시간(초)으로 타이머를 시작합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "시작 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "409", description = "이미 실행 중인 타이머 존재"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/start")
    ResponseEntity<TimerStateResponse> startTimer(
        @Parameter(
            description = "타이머 지속 시간 (초)",
            example = "300",
            required = true
        )
        @RequestParam long durationSeconds
    );

    @Operation(
        summary = "타이머 일시정지",
        description = "실행 중인 타이머를 일시정지합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "일시정지 성공"),
        @ApiResponse(responseCode = "409", description = "일시정지할 수 없는 상태"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/pause")
    ResponseEntity<TimerStateResponse> pauseTimer();

    @Operation(
        summary = "타이머 재개",
        description = "일시정지된 타이머를 재개합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "재개 성공"),
        @ApiResponse(responseCode = "409", description = "재개할 수 없는 상태"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/resume")
    ResponseEntity<TimerStateResponse> resumeTimer();

    @Operation(
        summary = "타이머 초기화",
        description = "타이머를 초기 상태로 리셋합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "초기화 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/reset")
    ResponseEntity<TimerStateResponse> resetTimer();
}