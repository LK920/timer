package com.acupofcoffee.timer.application.timer;

import com.acupofcoffee.timer.domain.timer.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TimerStateDtoTest {

    @Test
    @DisplayName("정지 상태 Timer로부터 DTO 생성")
    void shouldCreateDtoFromStoppedTimer() {
        // given
        Timer timer = new Timer(TimerId.generate());

        // when
        TimerStateDto dto = TimerStateDto.from(timer);

        // then
        assertFalse(dto.isRunning());
        assertEquals(0, dto.getStartTime());
        assertEquals(0, dto.getDurationSeconds());
        assertEquals(0, dto.getRemainingSeconds());
        assertEquals(0, dto.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("실행 중인 Timer로부터 DTO 생성")
    void shouldCreateDtoFromRunningTimer() {
        // given
        Timer timer = new Timer(TimerId.generate());
        Duration duration = Duration.ofMinutes(5);
        timer.start(duration);

        // when
        TimerStateDto dto = TimerStateDto.from(timer);

        // then
        assertTrue(dto.isRunning());
        assertTrue(dto.getStartTime() > 0);
        assertEquals(300, dto.getDurationSeconds());
        assertEquals(300, dto.getRemainingSeconds());
        assertTrue(dto.getCurrentRemainingSeconds() <= 300);
        assertTrue(dto.getCurrentRemainingSeconds() > 0);
    }

    @Test
    @DisplayName("일시정지 상태 Timer로부터 DTO 생성")
    void shouldCreateDtoFromPausedTimer() throws InterruptedException {
        // given
        Timer timer = new Timer(TimerId.generate());
        Duration duration = Duration.ofMinutes(5);
        timer.start(duration);
        Thread.sleep(100); // 잠시 실행
        timer.pause();

        // when
        TimerStateDto dto = TimerStateDto.from(timer);

        // then
        assertFalse(dto.isRunning());
        assertTrue(dto.getStartTime() > 0);
        assertEquals(300, dto.getDurationSeconds());
        assertTrue(dto.getRemainingSeconds() < 300);
        assertTrue(dto.getRemainingSeconds() > 0);
        assertEquals(dto.getRemainingSeconds(), dto.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("null 필드를 가진 Timer로부터 DTO 생성")
    void shouldHandleNullFieldsInTimer() {
        // given
        Timer timer = new Timer(TimerId.generate());
        // 초기 상태에서는 startedAt, duration 등이 null 또는 기본값

        // when
        TimerStateDto dto = TimerStateDto.from(timer);

        // then
        assertFalse(dto.isRunning());
        assertEquals(0, dto.getStartTime());
        assertEquals(0, dto.getDurationSeconds());
        assertEquals(0, dto.getRemainingSeconds());
        assertEquals(0, dto.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("완료된 Timer로부터 DTO 생성")
    void shouldCreateDtoFromCompletedTimer() {
        // given
        Timer timer = new Timer(TimerId.generate());
        timer.start(Duration.of(1)); // 1초

        // 시간 경과 시뮬레이션
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // when
        TimerStateDto dto = TimerStateDto.from(timer);

        // then
        assertTrue(dto.isRunning()); // 아직 실행 상태이지만
        assertEquals(0, dto.getCurrentRemainingSeconds()); // 남은 시간은 0
    }

    @Test
    @DisplayName("DTO의 모든 getter 메소드 테스트")
    void shouldTestAllGetterMethods() {
        // given
        Timer timer = new Timer(TimerId.generate());
        timer.start(Duration.ofMinutes(10));

        // when
        TimerStateDto dto = TimerStateDto.from(timer);

        // then - 모든 getter 호출하여 NPE 없음 확인
        assertNotNull(dto.isRunning());
        assertNotNull(dto.getStartTime());
        assertNotNull(dto.getDurationSeconds());
        assertNotNull(dto.getRemainingSeconds());
        assertNotNull(dto.getCurrentRemainingSeconds());

        // 값 검증
        assertTrue(dto.isRunning());
        assertTrue(dto.getStartTime() > 0);
        assertEquals(600, dto.getDurationSeconds());
        assertEquals(600, dto.getRemainingSeconds());
        assertTrue(dto.getCurrentRemainingSeconds() <= 600);
    }
}