package com.acupofcoffee.timer.presentation.timer;

import com.acupofcoffee.timer.application.timer.StartTimerCommand;
import com.acupofcoffee.timer.application.timer.TimerApplicationService;
import com.acupofcoffee.timer.application.timer.TimerStateDto;
import com.acupofcoffee.timer.presentation.timer.dto.TimerStateResponse;
import com.acupofcoffee.timer.presentation.timer.mapper.TimerPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimerControllerTest {

    @Mock
    private TimerApplicationService timerApplicationService;

    @Mock
    private TimerPresentationMapper mapper;

    private TimerController timerController;

    private TimerStateDto mockTimerStateDto;
    private TimerStateResponse mockTimerStateResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        timerController = new TimerController(timerApplicationService, mapper);

        mockTimerStateDto = new TimerStateDto(false, 0, 0, 0, 0);
        mockTimerStateResponse = new TimerStateResponse(false, 0, 0, 0, 0);
    }

    @Test
    @DisplayName("타이머 상태 조회 성공")
    void shouldGetTimerStateSuccessfully() {
        // given
        when(timerApplicationService.getCurrentTimerState()).thenReturn(mockTimerStateDto);
        when(mapper.toResponse(mockTimerStateDto)).thenReturn(mockTimerStateResponse);

        // when
        ResponseEntity<TimerStateResponse> response = timerController.getTimerState();

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTimerStateResponse, response.getBody());
        verify(timerApplicationService).getCurrentTimerState();
        verify(mapper).toResponse(mockTimerStateDto);
    }

    @Test
    @DisplayName("타이머 상태 조회 실패 - 서비스 예외")
    void shouldReturnInternalServerErrorWhenGetTimerStateFails() {
        // given
        when(timerApplicationService.getCurrentTimerState()).thenThrow(new RuntimeException("Service error"));

        // when
        ResponseEntity<TimerStateResponse> response = timerController.getTimerState();

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(timerApplicationService).getCurrentTimerState();
        verify(mapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("타이머 시작 성공")
    void shouldStartTimerSuccessfully() {
        // given
        long durationSeconds = 300;
        TimerStateDto runningTimerDto = new TimerStateDto(true, System.currentTimeMillis(), durationSeconds, durationSeconds, durationSeconds);
        TimerStateResponse runningTimerResponse = new TimerStateResponse(true, System.currentTimeMillis(), durationSeconds, durationSeconds, durationSeconds);

        when(timerApplicationService.startTimer(any(StartTimerCommand.class))).thenReturn(runningTimerDto);
        when(mapper.toCommand(any())).thenReturn(new StartTimerCommand(durationSeconds));
        when(mapper.toResponse(runningTimerDto)).thenReturn(runningTimerResponse);

        // when
        ResponseEntity<TimerStateResponse> response = timerController.startTimer(durationSeconds);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(runningTimerResponse, response.getBody());
        verify(timerApplicationService).startTimer(any(StartTimerCommand.class));
        verify(mapper).toCommand(any());
        verify(mapper).toResponse(runningTimerDto);
    }

    @Test
    @DisplayName("타이머 시작 실패 - 잘못된 파라미터")
    void shouldReturnBadRequestWhenStartTimerWithInvalidParameter() {
        // given
        long invalidDuration = 0;

        // when
        ResponseEntity<TimerStateResponse> response = timerController.startTimer(invalidDuration);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(timerApplicationService, never()).startTimer(any());
    }

    @Test
    @DisplayName("타이머 시작 실패 - 초과된 시간")
    void shouldReturnBadRequestWhenStartTimerWithExcessiveTime() {
        // given
        long excessiveDuration = 25 * 3600; // 25시간

        // when
        ResponseEntity<TimerStateResponse> response = timerController.startTimer(excessiveDuration);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(timerApplicationService, never()).startTimer(any());
    }

    @Test
    @DisplayName("타이머 시작 실패 - 이미 실행 중")
    void shouldReturnConflictWhenTimerAlreadyRunning() {
        // given
        long durationSeconds = 300;
        when(mapper.toCommand(any())).thenReturn(new StartTimerCommand(durationSeconds));
        when(timerApplicationService.startTimer(any(StartTimerCommand.class)))
            .thenThrow(new IllegalStateException("Timer is already running"));

        // when
        ResponseEntity<TimerStateResponse> response = timerController.startTimer(durationSeconds);

        // then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("타이머 시작 실패 - 서비스 예외")
    void shouldReturnInternalServerErrorWhenStartTimerServiceFails() {
        // given
        long durationSeconds = 300;
        when(mapper.toCommand(any())).thenReturn(new StartTimerCommand(durationSeconds));
        when(timerApplicationService.startTimer(any(StartTimerCommand.class)))
            .thenThrow(new RuntimeException("Service error"));

        // when
        ResponseEntity<TimerStateResponse> response = timerController.startTimer(durationSeconds);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("타이머 일시정지 성공")
    void shouldPauseTimerSuccessfully() {
        // given
        when(timerApplicationService.pauseTimer()).thenReturn(mockTimerStateDto);
        when(mapper.toResponse(mockTimerStateDto)).thenReturn(mockTimerStateResponse);

        // when
        ResponseEntity<TimerStateResponse> response = timerController.pauseTimer();

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTimerStateResponse, response.getBody());
        verify(timerApplicationService).pauseTimer();
        verify(mapper).toResponse(mockTimerStateDto);
    }

    @Test
    @DisplayName("타이머 일시정지 실패 - 일시정지 불가 상태")
    void shouldReturnConflictWhenCannotPauseTimer() {
        // given
        when(timerApplicationService.pauseTimer())
            .thenThrow(new IllegalStateException("Cannot pause timer"));

        // when
        ResponseEntity<TimerStateResponse> response = timerController.pauseTimer();

        // then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("타이머 일시정지 실패 - 서비스 예외")
    void shouldReturnInternalServerErrorWhenPauseTimerServiceFails() {
        // given
        when(timerApplicationService.pauseTimer()).thenThrow(new RuntimeException("Service error"));

        // when
        ResponseEntity<TimerStateResponse> response = timerController.pauseTimer();

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("타이머 재개 성공")
    void shouldResumeTimerSuccessfully() {
        // given
        when(timerApplicationService.resumeTimer()).thenReturn(mockTimerStateDto);
        when(mapper.toResponse(mockTimerStateDto)).thenReturn(mockTimerStateResponse);

        // when
        ResponseEntity<TimerStateResponse> response = timerController.resumeTimer();

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTimerStateResponse, response.getBody());
        verify(timerApplicationService).resumeTimer();
        verify(mapper).toResponse(mockTimerStateDto);
    }

    @Test
    @DisplayName("타이머 재개 실패 - 재개 불가 상태")
    void shouldReturnConflictWhenCannotResumeTimer() {
        // given
        when(timerApplicationService.resumeTimer())
            .thenThrow(new IllegalStateException("Cannot resume timer"));

        // when
        ResponseEntity<TimerStateResponse> response = timerController.resumeTimer();

        // then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("타이머 재개 실패 - 서비스 예외")
    void shouldReturnInternalServerErrorWhenResumeTimerServiceFails() {
        // given
        when(timerApplicationService.resumeTimer()).thenThrow(new RuntimeException("Service error"));

        // when
        ResponseEntity<TimerStateResponse> response = timerController.resumeTimer();

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("타이머 초기화 성공")
    void shouldResetTimerSuccessfully() {
        // given
        when(timerApplicationService.resetTimer()).thenReturn(mockTimerStateDto);
        when(mapper.toResponse(mockTimerStateDto)).thenReturn(mockTimerStateResponse);

        // when
        ResponseEntity<TimerStateResponse> response = timerController.resetTimer();

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTimerStateResponse, response.getBody());
        verify(timerApplicationService).resetTimer();
        verify(mapper).toResponse(mockTimerStateDto);
    }

    @Test
    @DisplayName("타이머 초기화 실패 - 서비스 예외")
    void shouldReturnInternalServerErrorWhenResetTimerServiceFails() {
        // given
        when(timerApplicationService.resetTimer()).thenThrow(new RuntimeException("Service error"));

        // when
        ResponseEntity<TimerStateResponse> response = timerController.resetTimer();

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}