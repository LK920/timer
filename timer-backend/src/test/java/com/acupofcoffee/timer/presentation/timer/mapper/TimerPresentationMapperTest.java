package com.acupofcoffee.timer.presentation.timer.mapper;

import com.acupofcoffee.timer.application.timer.StartTimerCommand;
import com.acupofcoffee.timer.application.timer.TimerStateDto;
import com.acupofcoffee.timer.presentation.timer.dto.StartTimerRequest;
import com.acupofcoffee.timer.presentation.timer.dto.TimerStateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class TimerPresentationMapperTest {

    private TimerPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TimerPresentationMapper();
    }

    @Test
    @DisplayName("StartTimerRequest를 StartTimerCommand로 변환")
    void shouldConvertStartTimerRequestToCommand() {
        // given
        long durationSeconds = 300;
        StartTimerRequest request = new StartTimerRequest(durationSeconds);

        // when
        StartTimerCommand command = mapper.toCommand(request);

        // then
        assertNotNull(command);
        assertEquals(durationSeconds, command.getDurationSeconds());
    }

    @Test
    @DisplayName("최소값 StartTimerRequest를 Command로 변환")
    void shouldConvertMinimumStartTimerRequestToCommand() {
        // given
        long minimumDuration = 1;
        StartTimerRequest request = new StartTimerRequest(minimumDuration);

        // when
        StartTimerCommand command = mapper.toCommand(request);

        // then
        assertNotNull(command);
        assertEquals(minimumDuration, command.getDurationSeconds());
    }

    @Test
    @DisplayName("최대값 StartTimerRequest를 Command로 변환")
    void shouldConvertMaximumStartTimerRequestToCommand() {
        // given
        long maximumDuration = 24 * 3600; // 24시간
        StartTimerRequest request = new StartTimerRequest(maximumDuration);

        // when
        StartTimerCommand command = mapper.toCommand(request);

        // then
        assertNotNull(command);
        assertEquals(maximumDuration, command.getDurationSeconds());
    }

    @Test
    @DisplayName("TimerStateDto를 TimerStateResponse로 변환")
    void shouldConvertTimerStateDtoToResponse() {
        // given
        boolean isRunning = true;
        long startTime = System.currentTimeMillis();
        long durationSeconds = 300;
        long remainingSeconds = 250;
        long currentRemainingSeconds = 240;

        TimerStateDto dto = new TimerStateDto(
            isRunning,
            startTime,
            durationSeconds,
            remainingSeconds,
            currentRemainingSeconds
        );

        // when
        TimerStateResponse response = mapper.toResponse(dto);

        // then
        assertNotNull(response);
        assertEquals(isRunning, response.isRunning());
        assertEquals(startTime, response.getStartTime());
        assertEquals(durationSeconds, response.getDurationSeconds());
        assertEquals(remainingSeconds, response.getRemainingSeconds());
        assertEquals(currentRemainingSeconds, response.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("정지된 타이머 DTO를 Response로 변환")
    void shouldConvertStoppedTimerDtoToResponse() {
        // given
        TimerStateDto stoppedDto = new TimerStateDto(
            false,
            0,
            0,
            0,
            0
        );

        // when
        TimerStateResponse response = mapper.toResponse(stoppedDto);

        // then
        assertNotNull(response);
        assertFalse(response.isRunning());
        assertEquals(0, response.getStartTime());
        assertEquals(0, response.getDurationSeconds());
        assertEquals(0, response.getRemainingSeconds());
        assertEquals(0, response.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("완료된 타이머 DTO를 Response로 변환")
    void shouldConvertCompletedTimerDtoToResponse() {
        // given
        long startTime = System.currentTimeMillis() - 300000; // 5분 전
        TimerStateDto completedDto = new TimerStateDto(
            true,  // 아직 실행 상태이지만
            startTime,
            300,
            0,     // 남은 시간 없음
            0      // 현재 남은 시간도 0
        );

        // when
        TimerStateResponse response = mapper.toResponse(completedDto);

        // then
        assertNotNull(response);
        assertTrue(response.isRunning());
        assertEquals(startTime, response.getStartTime());
        assertEquals(300, response.getDurationSeconds());
        assertEquals(0, response.getRemainingSeconds());
        assertEquals(0, response.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("일시정지된 타이머 DTO를 Response로 변환")
    void shouldConvertPausedTimerDtoToResponse() {
        // given
        long startTime = System.currentTimeMillis() - 60000; // 1분 전
        long remainingSeconds = 240;
        TimerStateDto pausedDto = new TimerStateDto(
            false, // 일시정지 상태
            startTime,
            300,
            remainingSeconds,
            remainingSeconds // 일시정지 시에는 같은 값
        );

        // when
        TimerStateResponse response = mapper.toResponse(pausedDto);

        // then
        assertNotNull(response);
        assertFalse(response.isRunning());
        assertEquals(startTime, response.getStartTime());
        assertEquals(300, response.getDurationSeconds());
        assertEquals(remainingSeconds, response.getRemainingSeconds());
        assertEquals(remainingSeconds, response.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("매퍼 변환 일관성 검증")
    void shouldMaintainConsistencyInConversion() {
        // given
        long originalDuration = 600;
        StartTimerRequest originalRequest = new StartTimerRequest(originalDuration);

        // when
        StartTimerCommand command = mapper.toCommand(originalRequest);

        // then
        assertEquals(originalRequest.getDurationSeconds(), command.getDurationSeconds());

        // given - 변환된 command로부터 dto 생성 시나리오
        TimerStateDto dto = new TimerStateDto(
            true,
            System.currentTimeMillis(),
            command.getDurationSeconds(),
            command.getDurationSeconds(),
            command.getDurationSeconds()
        );

        // when
        TimerStateResponse response = mapper.toResponse(dto);

        // then
        assertEquals(originalDuration, response.getDurationSeconds());
        assertEquals(dto.isRunning(), response.isRunning());
        assertEquals(dto.getStartTime(), response.getStartTime());
        assertEquals(dto.getRemainingSeconds(), response.getRemainingSeconds());
        assertEquals(dto.getCurrentRemainingSeconds(), response.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("매퍼 객체 상태 독립성 확인")
    void shouldMaintainStatelessBehavior() {
        // given
        StartTimerRequest request1 = new StartTimerRequest(300);
        StartTimerRequest request2 = new StartTimerRequest(600);

        // when
        StartTimerCommand command1 = mapper.toCommand(request1);
        StartTimerCommand command2 = mapper.toCommand(request2);

        // then
        assertNotEquals(command1.getDurationSeconds(), command2.getDurationSeconds());
        assertEquals(300, command1.getDurationSeconds());
        assertEquals(600, command2.getDurationSeconds());

        // 첫 번째 변환이 두 번째 변환에 영향을 주지 않음을 확인
        StartTimerCommand command1Again = mapper.toCommand(request1);
        assertEquals(command1.getDurationSeconds(), command1Again.getDurationSeconds());
    }
}