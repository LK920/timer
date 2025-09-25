package com.acupofcoffee.timer.infrastructure.timer;

import com.acupofcoffee.timer.domain.timer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTimerRepositoryTest {

    private InMemoryTimerRepository repository;
    private Timer timer1;
    private Timer timer2;
    private TimerId timerId1;
    private TimerId timerId2;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTimerRepository();
        timerId1 = TimerId.generate();
        timerId2 = TimerId.generate();
        timer1 = new Timer(timerId1);
        timer2 = new Timer(timerId2);
    }

    @Test
    @DisplayName("타이머 저장 성공")
    void shouldSaveTimerSuccessfully() {
        // when
        repository.save(timer1);

        // then
        Optional<Timer> found = repository.findById(timerId1);
        assertTrue(found.isPresent());
        assertEquals(timer1.getId(), found.get().getId());
    }

    @Test
    @DisplayName("타이머 저장 후 덮어쓰기")
    void shouldOverwriteTimerWhenSavingSameId() {
        // given
        repository.save(timer1);
        Timer modifiedTimer = new Timer(timerId1);
        modifiedTimer.start(Duration.ofMinutes(5));

        // when
        repository.save(modifiedTimer);

        // then
        Optional<Timer> found = repository.findById(timerId1);
        assertTrue(found.isPresent());
        assertEquals(TimerStatus.RUNNING, found.get().getStatus());
    }

    @Test
    @DisplayName("null 타이머 저장 시 예외 발생")
    void shouldThrowExceptionWhenSavingNullTimer() {
        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> repository.save(null)
        );
        assertEquals("Timer and TimerId cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("ID로 타이머 조회 성공")
    void shouldFindTimerByIdSuccessfully() {
        // given
        repository.save(timer1);

        // when
        Optional<Timer> found = repository.findById(timerId1);

        // then
        assertTrue(found.isPresent());
        assertEquals(timerId1, found.get().getId());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 empty 반환")
    void shouldReturnEmptyWhenTimerNotFound() {
        // given
        TimerId nonExistentId = TimerId.generate();

        // when
        Optional<Timer> found = repository.findById(nonExistentId);

        // then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("null ID로 조회 시 예외 발생")
    void shouldThrowExceptionWhenFindingByNullId() {
        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> repository.findById(null)
        );
        assertEquals("TimerId cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("활성 타이머 조회 - 타이머 존재")
    void shouldFindActiveTimerWhenTimerExists() {
        // given
        repository.save(timer1);

        // when
        Optional<Timer> activeTimer = repository.findActiveTimer();

        // then
        assertTrue(activeTimer.isPresent());
        assertEquals(timer1.getId(), activeTimer.get().getId());
    }

    @Test
    @DisplayName("활성 타이머 조회 - 타이머 없음")
    void shouldReturnEmptyWhenNoActiveTimer() {
        // when
        Optional<Timer> activeTimer = repository.findActiveTimer();

        // then
        assertFalse(activeTimer.isPresent());
    }

    @Test
    @DisplayName("여러 타이머 중 첫 번째 활성 타이머 조회")
    void shouldReturnFirstActiveTimerWhenMultipleTimersExist() {
        // given
        repository.save(timer1);
        repository.save(timer2);

        // when
        Optional<Timer> activeTimer = repository.findActiveTimer();

        // then
        assertTrue(activeTimer.isPresent());
        // ConcurrentHashMap의 순서는 보장되지 않으므로 존재하는지만 확인
        assertTrue(activeTimer.get().getId().equals(timerId1) ||
                   activeTimer.get().getId().equals(timerId2));
    }

    @Test
    @DisplayName("타이머 삭제 성공")
    void shouldDeleteTimerSuccessfully() {
        // given
        repository.save(timer1);
        assertTrue(repository.findById(timerId1).isPresent());

        // when
        repository.delete(timerId1);

        // then
        assertFalse(repository.findById(timerId1).isPresent());
    }

    @Test
    @DisplayName("존재하지 않는 타이머 삭제 시 아무 일 없음")
    void shouldDoNothingWhenDeletingNonExistentTimer() {
        // given
        TimerId nonExistentId = TimerId.generate();

        // when & then
        assertDoesNotThrow(() -> repository.delete(nonExistentId));
    }

    @Test
    @DisplayName("null ID로 삭제 시 예외 발생")
    void shouldThrowExceptionWhenDeletingWithNullId() {
        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> repository.delete(null)
        );
        assertEquals("TimerId cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("모든 타이머 삭제")
    void shouldDeleteAllTimers() {
        // given
        repository.save(timer1);
        repository.save(timer2);
        assertTrue(repository.findById(timerId1).isPresent());
        assertTrue(repository.findById(timerId2).isPresent());

        // when
        repository.deleteAll();

        // then
        assertFalse(repository.findById(timerId1).isPresent());
        assertFalse(repository.findById(timerId2).isPresent());
        assertFalse(repository.findActiveTimer().isPresent());
    }

    @Test
    @DisplayName("빈 리포지토리에서 모든 타이머 삭제")
    void shouldDoNothingWhenDeleteAllOnEmptyRepository() {
        // when & then
        assertDoesNotThrow(() -> repository.deleteAll());
        assertFalse(repository.findActiveTimer().isPresent());
    }

    @Test
    @DisplayName("동시성 테스트 - 여러 스레드에서 저장/조회")
    void shouldHandleConcurrentAccess() throws InterruptedException {
        // given
        int numThreads = 10;
        Thread[] threads = new Thread[numThreads];

        // when
        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                TimerId timerId = TimerId.of("timer-" + threadIndex);
                Timer timer = new Timer(timerId);
                repository.save(timer);
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // then
        for (int i = 0; i < numThreads; i++) {
            TimerId timerId = TimerId.of("timer-" + i);
            Optional<Timer> found = repository.findById(timerId);
            assertTrue(found.isPresent());
            assertEquals("timer-" + i, found.get().getId().getValue());
        }
    }
}