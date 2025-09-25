package com.acupofcoffee.timer;

import com.acupofcoffee.timer.application.timer.StartTimerCommand;
import com.acupofcoffee.timer.application.timer.TimerApplicationService;
import com.acupofcoffee.timer.application.timer.TimerStateDto;
import com.acupofcoffee.timer.domain.timer.Duration;
import com.acupofcoffee.timer.domain.timer.Timer;
import com.acupofcoffee.timer.domain.timer.TimerId;
import com.acupofcoffee.timer.domain.timer.TimerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TimerApplicationIntegrationTest {

    @Autowired
    private TimerApplicationService timerApplicationService;

    @Autowired
    private TimerRepository timerRepository;

    private final TimerId defaultTimerId = TimerId.of("default-timer");

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 리포지토리 초기화
        timerRepository.deleteAll();
    }

    @Test
    @DisplayName("전체 워크플로우 통합 테스트 - 타이머 시작부터 완료까지")
    void shouldCompleteFullTimerWorkflow() throws InterruptedException {
        // 1. 초기 상태 조회
        TimerStateDto initialState = timerApplicationService.getCurrentTimerState();
        assertFalse(initialState.isRunning());
        assertEquals(0, initialState.getDurationSeconds());

        // 2. 타이머 시작
        StartTimerCommand startCommand = new StartTimerCommand(2); // 2초
        TimerStateDto runningState = timerApplicationService.startTimer(startCommand);
        assertTrue(runningState.isRunning());
        assertEquals(2, runningState.getDurationSeconds());
        assertEquals(2, runningState.getRemainingSeconds());

        // 3. 실행 중 상태 확인
        Thread.sleep(500);
        TimerStateDto runningState2 = timerApplicationService.getCurrentTimerState();
        assertTrue(runningState2.isRunning());
        assertTrue(runningState2.getCurrentRemainingSeconds() < 2);
        assertTrue(runningState2.getCurrentRemainingSeconds() > 0);

        // 4. 일시정지
        TimerStateDto pausedState = timerApplicationService.pauseTimer();
        assertFalse(pausedState.isRunning());
        assertTrue(pausedState.getRemainingSeconds() > 0);

        // 5. 재개
        TimerStateDto resumedState = timerApplicationService.resumeTimer();
        assertTrue(resumedState.isRunning());

        // 6. 완료까지 대기
        Thread.sleep(2500);
        TimerStateDto finalState = timerApplicationService.getCurrentTimerState();
        // 완료된 타이머는 자동으로 정지 상태로 전환
        assertFalse(finalState.isRunning());
        assertEquals(0, finalState.getCurrentRemainingSeconds());

        // 7. 초기화
        TimerStateDto resetState = timerApplicationService.resetTimer();
        assertFalse(resetState.isRunning());
        assertEquals(0, resetState.getDurationSeconds());
    }

    @Test
    @DisplayName("타이머 생성 및 저장 통합 테스트")
    void shouldCreateAndSaveTimerIntegration() {
        // when
        TimerStateDto state = timerApplicationService.getCurrentTimerState();

        // then
        assertNotNull(state);
        assertFalse(state.isRunning());

        // 리포지토리에 타이머가 저장되었는지 확인
        var savedTimer = timerRepository.findById(defaultTimerId);
        assertTrue(savedTimer.isPresent());
        assertEquals(defaultTimerId, savedTimer.get().getId());
    }

    @Test
    @DisplayName("타이머 시작 실패 후 상태 일관성 테스트")
    void shouldMaintainConsistencyAfterStartFailure() {
        // given - 타이머를 먼저 시작
        timerApplicationService.startTimer(new StartTimerCommand(300));

        // when - 이미 실행 중인 타이머 시작 시도
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> timerApplicationService.startTimer(new StartTimerCommand(600))
        );

        // then
        assertEquals("Cannot start timer in current state", exception.getMessage());

        // 상태 일관성 확인
        TimerStateDto state = timerApplicationService.getCurrentTimerState();
        assertTrue(state.isRunning());
        assertEquals(300, state.getDurationSeconds()); // 원래 duration 유지
    }

    @Test
    @DisplayName("타이머 일시정지/재개 반복 테스트")
    void shouldHandleMultiplePauseResumeCycles() throws InterruptedException {
        // given
        timerApplicationService.startTimer(new StartTimerCommand(10));

        // 첫 번째 일시정지/재개 사이클
        Thread.sleep(100);
        TimerStateDto pausedState1 = timerApplicationService.pauseTimer();
        assertFalse(pausedState1.isRunning());
        long remaining1 = pausedState1.getRemainingSeconds();

        TimerStateDto resumedState1 = timerApplicationService.resumeTimer();
        assertTrue(resumedState1.isRunning());

        // 두 번째 일시정지/재개 사이클
        Thread.sleep(100);
        TimerStateDto pausedState2 = timerApplicationService.pauseTimer();
        assertFalse(pausedState2.isRunning());
        long remaining2 = pausedState2.getRemainingSeconds();

        // 시간이 흘렀으므로 남은 시간이 감소해야 함
        assertTrue(remaining2 < remaining1);

        TimerStateDto resumedState2 = timerApplicationService.resumeTimer();
        assertTrue(resumedState2.isRunning());
    }

    @Test
    @DisplayName("리포지토리 저장/조회 통합 테스트")
    void shouldIntegrateWithRepositoryCorrectly() {
        // given - 직접 타이머를 리포지토리에 저장
        Timer directTimer = new Timer(defaultTimerId);
        directTimer.start(Duration.ofMinutes(5));
        timerRepository.save(directTimer);

        // when - 서비스에서 조회
        TimerStateDto state = timerApplicationService.getCurrentTimerState();

        // then
        assertTrue(state.isRunning());
        assertEquals(300, state.getDurationSeconds());

        // 도메인 서비스가 자동 검증을 수행했는지 확인
        var savedTimer = timerRepository.findById(defaultTimerId);
        assertTrue(savedTimer.isPresent());
    }

    @Test
    @DisplayName("동시성 시나리오 통합 테스트")
    void shouldHandleConcurrentOperations() throws InterruptedException {
        // given
        timerApplicationService.startTimer(new StartTimerCommand(5));

        // when - 여러 스레드에서 상태 조회
        Thread[] threads = new Thread[5];
        TimerStateDto[] results = new TimerStateDto[5];

        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = timerApplicationService.getCurrentTimerState();
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // then - 모든 결과가 일관성 있게 실행 중 상태를 보여야 함
        for (TimerStateDto result : results) {
            assertTrue(result.isRunning());
            assertEquals(5, result.getDurationSeconds());
        }
    }

    @Test
    @DisplayName("예외 상황 후 복구 테스트")
    void shouldRecoverFromExceptionScenarios() {
        // given - 정지 상태에서 일시정지 시도 (예외 발생)
        IllegalStateException pauseException = assertThrows(
            IllegalStateException.class,
            () -> timerApplicationService.pauseTimer()
        );
        assertEquals("Cannot pause timer in current state", pauseException.getMessage());

        // when - 정상 동작으로 복구
        timerApplicationService.startTimer(new StartTimerCommand(300));
        TimerStateDto state = timerApplicationService.getCurrentTimerState();

        // then - 정상 상태 확인
        assertTrue(state.isRunning());
        assertEquals(300, state.getDurationSeconds());

        // 정상적으로 일시정지 가능
        assertDoesNotThrow(() -> timerApplicationService.pauseTimer());
    }

    @Test
    @DisplayName("타이머 완료 후 자동 정지 통합 테스트")
    void shouldAutoStopAfterCompletion() throws InterruptedException {
        // given
        timerApplicationService.startTimer(new StartTimerCommand(1)); // 1초

        // when - 완료까지 대기
        Thread.sleep(1200);
        TimerStateDto completedState = timerApplicationService.getCurrentTimerState();

        // then - 자동으로 정지 상태로 전환
        assertFalse(completedState.isRunning());
        assertEquals(0, completedState.getCurrentRemainingSeconds());
        assertEquals(0, completedState.getDurationSeconds());

        // 리포지토리 상태도 일치하는지 확인
        var timerFromRepo = timerRepository.findById(defaultTimerId);
        assertTrue(timerFromRepo.isPresent());
        assertFalse(timerFromRepo.get().isRunning());
    }

    @Test
    @DisplayName("리셋 후 새로운 타이머 시작 통합 테스트")
    void shouldStartNewTimerAfterReset() {
        // given - 타이머 시작 후 리셋
        timerApplicationService.startTimer(new StartTimerCommand(300));
        timerApplicationService.resetTimer();

        // when - 새로운 타이머 시작
        TimerStateDto newTimerState = timerApplicationService.startTimer(new StartTimerCommand(600));

        // then
        assertTrue(newTimerState.isRunning());
        assertEquals(600, newTimerState.getDurationSeconds());
        assertEquals(600, newTimerState.getRemainingSeconds());

        // 이전 타이머 상태가 완전히 초기화되었는지 확인
        TimerStateDto currentState = timerApplicationService.getCurrentTimerState();
        assertEquals(600, currentState.getDurationSeconds());
    }
}