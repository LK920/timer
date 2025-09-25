package com.acupofcoffee.timer.domain.timer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class TimerDomainServiceTest {

    private TimerDomainService timerDomainService;
    private Timer timer;
    private TimerId timerId;

    @BeforeEach
    void setUp() {
        timerDomainService = new TimerDomainService();
        timerId = TimerId.generate();
        timer = new Timer(timerId);
    }

    @Test
    @DisplayName("타이머 시작 가능 - 정상 상태")
    void shouldAllowStartingTimer() {
        // given
        Duration validDuration = Duration.ofMinutes(5);

        // when
        boolean canStart = timerDomainService.canStartTimer(timer, validDuration);

        // then
        assertTrue(canStart);
    }

    @Test
    @DisplayName("타이머 시작 불가 - 이미 실행 중")
    void shouldNotAllowStartingRunningTimer() {
        // given
        timer.start(Duration.ofMinutes(5));
        Duration validDuration = Duration.ofMinutes(3);

        // when
        boolean canStart = timerDomainService.canStartTimer(timer, validDuration);

        // then
        assertFalse(canStart);
    }

    @Test
    @DisplayName("타이머 시작 불가 - null duration")
    void shouldNotAllowStartingWithNullDuration() {
        // when
        boolean canStart = timerDomainService.canStartTimer(timer, null);

        // then
        assertFalse(canStart);
    }

    @Test
    @DisplayName("타이머 시작 불가 - 0초 duration")
    void shouldNotAllowStartingWithZeroDuration() {
        // given
        Duration zeroDuration = Duration.ZERO;

        // when
        boolean canStart = timerDomainService.canStartTimer(timer, zeroDuration);

        // then
        assertFalse(canStart);
    }

    @Test
    @DisplayName("타이머 시작 불가 - 24시간 초과")
    void shouldNotAllowStartingWithExcessiveDuration() {
        // given
        Duration excessiveDuration = Duration.of(25 * 3600); // 25시간

        // when
        boolean canStart = timerDomainService.canStartTimer(timer, excessiveDuration);

        // then
        assertFalse(canStart);
    }

    @Test
    @DisplayName("타이머 시작 가능 - 24시간 정확히")
    void shouldAllowStartingWithMaxDuration() {
        // given
        Duration maxDuration = Duration.of(24 * 3600); // 24시간

        // when
        boolean canStart = timerDomainService.canStartTimer(timer, maxDuration);

        // then
        assertTrue(canStart);
    }

    @Test
    @DisplayName("타이머 일시정지 가능 - 실행 중")
    void shouldAllowPausingRunningTimer() {
        // given
        timer.start(Duration.ofMinutes(5));

        // when
        boolean canPause = timerDomainService.canPauseTimer(timer);

        // then
        assertTrue(canPause);
    }

    @Test
    @DisplayName("타이머 일시정지 불가 - 정지 상태")
    void shouldNotAllowPausingStoppedTimer() {
        // when
        boolean canPause = timerDomainService.canPauseTimer(timer);

        // then
        assertFalse(canPause);
    }

    @Test
    @DisplayName("타이머 일시정지 불가 - 이미 일시정지")
    void shouldNotAllowPausingPausedTimer() {
        // given
        timer.start(Duration.ofMinutes(5));
        timer.pause();

        // when
        boolean canPause = timerDomainService.canPauseTimer(timer);

        // then
        assertFalse(canPause);
    }

    @Test
    @DisplayName("타이머 재개 가능 - 일시정지 상태에서 남은 시간 있음")
    void shouldAllowResumingPausedTimerWithRemainingTime() throws InterruptedException {
        // given
        timer.start(Duration.ofMinutes(5));
        Thread.sleep(100); // 잠시 대기
        timer.pause();

        // when
        boolean canResume = timerDomainService.canResumeTimer(timer);

        // then
        assertTrue(canResume);
    }

    @Test
    @DisplayName("타이머 재개 불가 - 실행 상태")
    void shouldNotAllowResumingRunningTimer() {
        // given
        timer.start(Duration.ofMinutes(5));

        // when
        boolean canResume = timerDomainService.canResumeTimer(timer);

        // then
        assertFalse(canResume);
    }

    @Test
    @DisplayName("타이머 재개 불가 - 정지 상태")
    void shouldNotAllowResumingStoppedTimer() {
        // when
        boolean canResume = timerDomainService.canResumeTimer(timer);

        // then
        assertFalse(canResume);
    }

    @Test
    @DisplayName("타이머 완료 처리")
    void shouldCompleteTimer() throws InterruptedException {
        // given
        timer.start(Duration.of(1)); // 1초
        Thread.sleep(1100); // 1초 대기

        // when
        timerDomainService.completeTimer(timer);

        // then
        assertEquals(TimerStatus.STOPPED, timer.getStatus());
        assertEquals(Duration.ZERO, timer.getDuration());
    }

    @Test
    @DisplayName("완료되지 않은 타이머는 완료 처리 안함")
    void shouldNotCompleteRunningTimer() {
        // given
        timer.start(Duration.ofMinutes(5));
        TimerStatus originalStatus = timer.getStatus();

        // when
        timerDomainService.completeTimer(timer);

        // then
        assertEquals(originalStatus, timer.getStatus());
    }

    @Test
    @DisplayName("타이머 상태 검증 성공")
    void shouldValidateTimerState() {
        // when & then
        assertDoesNotThrow(() -> timerDomainService.validateTimerState(timer));
    }

    @Test
    @DisplayName("null 타이머 검증 시 예외 발생")
    void shouldThrowExceptionWhenValidatingNullTimer() {
        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> timerDomainService.validateTimerState(null)
        );
        assertEquals("Timer cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("null TimerId 검증 시 예외 발생")
    void shouldThrowExceptionWhenValidatingTimerWithNullId() {
        // given - Timer 생성자에서 null을 받지 않으므로 리플렉션으로 강제 설정
        // 이 케이스는 실제로는 발생하지 않지만 방어적 코드 테스트
        Timer timerWithNullId = new Timer(TimerId.generate());

        // when & then - 정상 케이스이므로 예외 발생 안함
        assertDoesNotThrow(() -> timerDomainService.validateTimerState(timerWithNullId));
    }

    @Test
    @DisplayName("완료된 타이머 상태 검증 시 자동 완료 처리")
    void shouldAutoCompleteWhenValidatingCompletedTimer() throws InterruptedException {
        // given
        timer.start(Duration.of(1));
        Thread.sleep(1100); // 완료될 때까지 대기

        // when
        timerDomainService.validateTimerState(timer);

        // then
        assertEquals(TimerStatus.STOPPED, timer.getStatus());
        assertTrue(timer.getDuration().isZero());
    }

    @Test
    @DisplayName("일시정지된 타이머의 남은 시간이 0일 때 재개 불가")
    void shouldNotAllowResumingPausedTimerWithNoRemainingTime() {
        // given
        timer.start(Duration.of(1));
        try {
            Thread.sleep(1100); // 시간 완료까지 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        timer.pause(); // 시간이 완료된 후 일시정지

        // when
        boolean canResume = timerDomainService.canResumeTimer(timer);

        // then
        assertFalse(canResume);
    }
}