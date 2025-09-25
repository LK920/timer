package com.acupofcoffee.timer.application.timer;

import com.acupofcoffee.timer.domain.timer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimerApplicationServiceTest {

    @Mock
    private TimerRepository timerRepository;

    @Mock
    private TimerDomainService timerDomainService;

    private TimerApplicationService timerApplicationService;

    private Timer timer;
    private TimerId defaultTimerId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        timerApplicationService = new TimerApplicationService(timerRepository, timerDomainService);

        defaultTimerId = TimerId.of("default-timer");
        timer = new Timer(defaultTimerId);
    }

    @Test
    @DisplayName("현재 타이머 상태 조회 - 기존 타이머 존재")
    void shouldGetCurrentTimerStateWhenTimerExists() {
        // given
        when(timerRepository.findById(defaultTimerId)).thenReturn(Optional.of(timer));

        // when
        TimerStateDto result = timerApplicationService.getCurrentTimerState();

        // then
        assertNotNull(result);
        assertEquals(timer.getStatus() == TimerStatus.RUNNING, result.isRunning());
        verify(timerDomainService).validateTimerState(timer);
        verify(timerRepository).findById(defaultTimerId);
    }

    @Test
    @DisplayName("현재 타이머 상태 조회 - 타이머 없을 때 새로 생성")
    void shouldCreateNewTimerWhenNotExists() {
        // given
        when(timerRepository.findById(defaultTimerId)).thenReturn(Optional.empty());

        // when
        TimerStateDto result = timerApplicationService.getCurrentTimerState();

        // then
        assertNotNull(result);
        assertFalse(result.isRunning());
        assertEquals(0, result.getDurationSeconds());
        verify(timerRepository).findById(defaultTimerId);
        verify(timerRepository).save(any(Timer.class));
        verify(timerDomainService).validateTimerState(any(Timer.class));
    }

    @Test
    @DisplayName("타이머 시작 성공")
    void shouldStartTimer() {
        // given
        long durationSeconds = 300;
        StartTimerCommand command = new StartTimerCommand(durationSeconds);
        Duration duration = Duration.of(durationSeconds);

        when(timerRepository.findById(defaultTimerId)).thenReturn(Optional.of(timer));
        when(timerDomainService.canStartTimer(timer, duration)).thenReturn(true);

        // when
        TimerStateDto result = timerApplicationService.startTimer(command);

        // then
        assertNotNull(result);
        assertTrue(result.isRunning());
        assertEquals(durationSeconds, result.getDurationSeconds());
        verify(timerRepository).save(timer);
        verify(timerDomainService).canStartTimer(timer, duration);
    }

    @Test
    @DisplayName("타이머 시작 실패 - 시작 불가 상태")
    void shouldFailToStartTimerWhenCannotStart() {
        // given
        long durationSeconds = 300;
        StartTimerCommand command = new StartTimerCommand(durationSeconds);
        Duration duration = Duration.of(durationSeconds);

        when(timerRepository.findById(defaultTimerId)).thenReturn(Optional.of(timer));
        when(timerDomainService.canStartTimer(timer, duration)).thenReturn(false);

        // when & then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> timerApplicationService.startTimer(command)
        );
        assertEquals("Cannot start timer in current state", exception.getMessage());
        verify(timerRepository, never()).save(timer);
    }

    @Test
    @DisplayName("타이머 시작 - 새 타이머 생성 후 시작")
    void shouldCreateNewTimerAndStart() {
        // given
        long durationSeconds = 300;
        StartTimerCommand command = new StartTimerCommand(durationSeconds);
        Duration duration = Duration.of(durationSeconds);

        when(timerRepository.findById(defaultTimerId)).thenReturn(Optional.empty());
        when(timerDomainService.canStartTimer(any(Timer.class), eq(duration))).thenReturn(true);

        // when
        TimerStateDto result = timerApplicationService.startTimer(command);

        // then
        assertNotNull(result);
        assertTrue(result.isRunning());
        verify(timerRepository, times(2)).save(any(Timer.class)); // 생성 시 1번, 시작 시 1번
    }

    @Test
    @DisplayName("타이머 일시정지 성공")
    void shouldPauseTimer() {
        // given
        timer.start(Duration.ofMinutes(5));
        when(timerRepository.findById(defaultTimerId)).thenReturn(Optional.of(timer));
        when(timerDomainService.canPauseTimer(timer)).thenReturn(true);

        // when
        TimerStateDto result = timerApplicationService.pauseTimer();

        // then
        assertNotNull(result);
        verify(timerRepository).save(timer);
        verify(timerDomainService).canPauseTimer(timer);
    }

    @Test
    @DisplayName("타이머 일시정지 실패 - 일시정지 불가 상태")
    void shouldFailToPauseTimerWhenCannotPause() {
        // given
        when(timerRepository.findById(defaultTimerId)).thenReturn(Optional.of(timer));
        when(timerDomainService.canPauseTimer(timer)).thenReturn(false);

        // when & then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> timerApplicationService.pauseTimer()
        );
        assertEquals("Cannot pause timer in current state", exception.getMessage());
        verify(timerRepository, never()).save(timer);
    }

    @Test
    @DisplayName("타이머 재개 성공")
    void shouldResumeTimer() throws InterruptedException {
        // given
        timer.start(Duration.ofMinutes(5));
        Thread.sleep(100);
        timer.pause();

        when(timerRepository.findById(defaultTimerId)).thenReturn(Optional.of(timer));
        when(timerDomainService.canResumeTimer(timer)).thenReturn(true);

        // when
        TimerStateDto result = timerApplicationService.resumeTimer();

        // then
        assertNotNull(result);
        verify(timerRepository).save(timer);
        verify(timerDomainService).canResumeTimer(timer);
    }

    @Test
    @DisplayName("타이머 재개 실패 - 재개 불가 상태")
    void shouldFailToResumeTimerWhenCannotResume() {
        // given
        when(timerRepository.findById(defaultTimerId)).thenReturn(Optional.of(timer));
        when(timerDomainService.canResumeTimer(timer)).thenReturn(false);

        // when & then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> timerApplicationService.resumeTimer()
        );
        assertEquals("Cannot resume timer in current state", exception.getMessage());
        verify(timerRepository, never()).save(timer);
    }

    @Test
    @DisplayName("타이머 초기화 성공")
    void shouldResetTimer() {
        // given
        timer.start(Duration.ofMinutes(5));
        when(timerRepository.findById(defaultTimerId)).thenReturn(Optional.of(timer));

        // when
        TimerStateDto result = timerApplicationService.resetTimer();

        // then
        assertNotNull(result);
        assertFalse(result.isRunning());
        assertEquals(0, result.getDurationSeconds());
        assertEquals(0, result.getCurrentRemainingSeconds());
        verify(timerRepository).save(timer);
    }

    @Test
    @DisplayName("타이머 초기화 - 새 타이머 생성")
    void shouldCreateNewTimerWhenResetAndNotExists() {
        // given
        when(timerRepository.findById(defaultTimerId)).thenReturn(Optional.empty());

        // when
        TimerStateDto result = timerApplicationService.resetTimer();

        // then
        assertNotNull(result);
        assertFalse(result.isRunning());
        verify(timerRepository, times(2)).save(any(Timer.class)); // 생성 시 1번, 리셋 시 1번
    }
}