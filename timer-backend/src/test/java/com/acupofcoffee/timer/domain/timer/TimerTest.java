package com.acupofcoffee.timer.domain.timer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TimerTest {

    private Timer timer;
    private TimerId timerId;
    private Duration fiveMinutes;
    private Duration twoMinutes;

    @BeforeEach
    void setUp() {
        timerId = TimerId.generate();
        timer = new Timer(timerId);
        fiveMinutes = Duration.ofMinutes(5);
        twoMinutes = Duration.ofMinutes(2);
    }

    @Test
    @DisplayName("타이머 생성 시 초기 상태 확인")
    void shouldCreateTimerWithInitialState() {
        // then
        assertEquals(timerId, timer.getId());
        assertEquals(TimerStatus.STOPPED, timer.getStatus());
        assertEquals(Duration.ZERO, timer.getDuration());
        assertEquals(Duration.ZERO, timer.getRemainingTime());
        assertNull(timer.getStartedAt());
        assertEquals(Duration.ZERO, timer.getCurrentRemainingTime());
        assertFalse(timer.isCompleted());
    }

    @Test
    @DisplayName("타이머 시작 성공")
    void shouldStartTimer() {
        // when
        timer.start(fiveMinutes);

        // then
        assertEquals(TimerStatus.RUNNING, timer.getStatus());
        assertEquals(fiveMinutes, timer.getDuration());
        assertEquals(fiveMinutes, timer.getRemainingTime());
        assertNotNull(timer.getStartedAt());
        assertTrue(timer.getCurrentRemainingTime().getSeconds() <= fiveMinutes.getSeconds());
        assertFalse(timer.isCompleted());
    }

    @Test
    @DisplayName("이미 실행 중인 타이머 시작 시도 시 예외 발생")
    void shouldThrowExceptionWhenStartingRunningTimer() {
        // given
        timer.start(fiveMinutes);

        // when & then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> timer.start(twoMinutes)
        );
        assertEquals("Timer is already running", exception.getMessage());
    }

    @Test
    @DisplayName("실행 중인 타이머 일시정지 성공")
    void shouldPauseRunningTimer() throws InterruptedException {
        // given
        timer.start(fiveMinutes);
        Thread.sleep(100); // 잠시 대기하여 경과 시간 확보

        // when
        timer.pause();

        // then
        assertEquals(TimerStatus.PAUSED, timer.getStatus());
        assertTrue(timer.getRemainingTime().getSeconds() < fiveMinutes.getSeconds());
        assertTrue(timer.getRemainingTime().isPositive());
    }

    @Test
    @DisplayName("실행 중이 아닌 타이머 일시정지 시도 시 예외 발생")
    void shouldThrowExceptionWhenPausingNonRunningTimer() {
        // when & then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> timer.pause()
        );
        assertEquals("Timer is not running", exception.getMessage());
    }

    @Test
    @DisplayName("일시정지된 타이머 재개 성공")
    void shouldResumeTimer() throws InterruptedException {
        // given
        timer.start(fiveMinutes);
        Thread.sleep(100);
        timer.pause();
        Duration remainingBeforeResume = timer.getRemainingTime();

        // when
        timer.resume();

        // then
        assertEquals(TimerStatus.RUNNING, timer.getStatus());
        assertEquals(remainingBeforeResume, timer.getDuration());
        assertNotNull(timer.getStartedAt());
    }

    @Test
    @DisplayName("일시정지 상태가 아닌 타이머 재개 시도 시 예외 발생")
    void shouldThrowExceptionWhenResumingNonPausedTimer() {
        // when & then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> timer.resume()
        );
        assertEquals("Cannot resume timer in current state", exception.getMessage());
    }

    @Test
    @DisplayName("남은 시간이 없는 타이머 재개 시도 시 예외 발생")
    void shouldThrowExceptionWhenResumingTimerWithNoRemainingTime() {
        // given
        timer.start(Duration.of(1));
        try {
            Thread.sleep(1100); // 1초보다 조금 더 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        timer.pause();

        // when & then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> timer.resume()
        );
        assertEquals("Cannot resume timer in current state", exception.getMessage());
    }

    @Test
    @DisplayName("타이머 초기화 성공")
    void shouldResetTimer() {
        // given
        timer.start(fiveMinutes);

        // when
        timer.reset();

        // then
        assertEquals(TimerStatus.STOPPED, timer.getStatus());
        assertEquals(Duration.ZERO, timer.getDuration());
        assertEquals(Duration.ZERO, timer.getRemainingTime());
        assertNull(timer.getStartedAt());
        assertEquals(Duration.ZERO, timer.getCurrentRemainingTime());
        assertFalse(timer.isCompleted());
    }

    @Test
    @DisplayName("타이머 완료 상태 확인")
    void shouldDetectCompletedTimer() {
        // given
        timer.start(Duration.of(1));

        // when - 시간이 지나도록 시뮬레이션
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // then
        assertTrue(timer.isCompleted());
        assertEquals(Duration.ZERO, timer.getCurrentRemainingTime());
    }

    @Test
    @DisplayName("일시정지 상태에서 현재 남은 시간 계산")
    void shouldCalculateCurrentRemainingTimeWhenPaused() throws InterruptedException {
        // given
        timer.start(fiveMinutes);
        Thread.sleep(100);
        timer.pause();

        // when
        Duration currentRemaining = timer.getCurrentRemainingTime();

        // then
        assertEquals(timer.getRemainingTime(), currentRemaining);
        assertTrue(currentRemaining.getSeconds() < fiveMinutes.getSeconds());
    }
}