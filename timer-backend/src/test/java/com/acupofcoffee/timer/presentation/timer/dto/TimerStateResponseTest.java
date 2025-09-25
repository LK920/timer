package com.acupofcoffee.timer.presentation.timer.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class TimerStateResponseTest {

    @Test
    @DisplayName("실행 중인 타이머 응답 생성")
    void shouldCreateRunningTimerResponse() {
        // given
        boolean running = true;
        long startTime = System.currentTimeMillis();
        long durationSeconds = 300;
        long remainingSeconds = 250;
        long currentRemainingSeconds = 240;

        // when
        TimerStateResponse response = new TimerStateResponse(
            running, startTime, durationSeconds, remainingSeconds, currentRemainingSeconds
        );

        // then
        assertTrue(response.isRunning());
        assertEquals(startTime, response.getStartTime());
        assertEquals(durationSeconds, response.getDurationSeconds());
        assertEquals(remainingSeconds, response.getRemainingSeconds());
        assertEquals(currentRemainingSeconds, response.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("정지된 타이머 응답 생성")
    void shouldCreateStoppedTimerResponse() {
        // when
        TimerStateResponse response = new TimerStateResponse(
            false, 0, 0, 0, 0
        );

        // then
        assertFalse(response.isRunning());
        assertEquals(0, response.getStartTime());
        assertEquals(0, response.getDurationSeconds());
        assertEquals(0, response.getRemainingSeconds());
        assertEquals(0, response.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("일시정지된 타이머 응답 생성")
    void shouldCreatePausedTimerResponse() {
        // given
        long startTime = System.currentTimeMillis() - 60000; // 1분 전
        long durationSeconds = 300;
        long remainingSeconds = 240;

        // when
        TimerStateResponse response = new TimerStateResponse(
            false, startTime, durationSeconds, remainingSeconds, remainingSeconds
        );

        // then
        assertFalse(response.isRunning());
        assertEquals(startTime, response.getStartTime());
        assertEquals(durationSeconds, response.getDurationSeconds());
        assertEquals(remainingSeconds, response.getRemainingSeconds());
        assertEquals(remainingSeconds, response.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("완료된 타이머 응답 생성")
    void shouldCreateCompletedTimerResponse() {
        // given
        long startTime = System.currentTimeMillis() - 300000; // 5분 전
        long durationSeconds = 300;

        // when
        TimerStateResponse response = new TimerStateResponse(
            true, startTime, durationSeconds, 0, 0
        );

        // then
        assertTrue(response.isRunning());
        assertEquals(startTime, response.getStartTime());
        assertEquals(durationSeconds, response.getDurationSeconds());
        assertEquals(0, response.getRemainingSeconds());
        assertEquals(0, response.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("최소값 타이머 응답 생성")
    void shouldCreateMinimumTimerResponse() {
        // when
        TimerStateResponse response = new TimerStateResponse(
            true, 1, 1, 1, 1
        );

        // then
        assertTrue(response.isRunning());
        assertEquals(1, response.getStartTime());
        assertEquals(1, response.getDurationSeconds());
        assertEquals(1, response.getRemainingSeconds());
        assertEquals(1, response.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("최대값 타이머 응답 생성")
    void shouldCreateMaximumTimerResponse() {
        // given
        long maxDuration = 24 * 3600; // 24시간
        long maxTime = Long.MAX_VALUE;

        // when
        TimerStateResponse response = new TimerStateResponse(
            true, maxTime, maxDuration, maxDuration, maxDuration
        );

        // then
        assertTrue(response.isRunning());
        assertEquals(maxTime, response.getStartTime());
        assertEquals(maxDuration, response.getDurationSeconds());
        assertEquals(maxDuration, response.getRemainingSeconds());
        assertEquals(maxDuration, response.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("응답 객체 불변성 확인")
    void shouldBeImmutable() {
        // given
        long originalStartTime = System.currentTimeMillis();
        long originalDuration = 300;
        TimerStateResponse response = new TimerStateResponse(
            true, originalStartTime, originalDuration, 250, 240
        );

        // when
        long retrievedStartTime = response.getStartTime();
        long retrievedDuration = response.getDurationSeconds();
        boolean retrievedRunning = response.isRunning();

        // then
        assertEquals(originalStartTime, retrievedStartTime);
        assertEquals(originalDuration, retrievedDuration);
        assertTrue(retrievedRunning);

        // 여러 번 호출해도 같은 값
        assertEquals(retrievedStartTime, response.getStartTime());
        assertEquals(retrievedDuration, response.getDurationSeconds());
        assertEquals(retrievedRunning, response.isRunning());
    }

    @Test
    @DisplayName("남은 시간과 현재 남은 시간의 차이 검증")
    void shouldHandleDifferentRemainingTimes() {
        // given - 실행 중이면서 시간이 흐른 상황
        long remainingSeconds = 250; // 일시정지 시점의 남은 시간
        long currentRemainingSeconds = 240; // 현재 실제 남은 시간

        // when
        TimerStateResponse response = new TimerStateResponse(
            true, System.currentTimeMillis(), 300, remainingSeconds, currentRemainingSeconds
        );

        // then
        assertTrue(response.getRemainingSeconds() > response.getCurrentRemainingSeconds());
        assertEquals(remainingSeconds, response.getRemainingSeconds());
        assertEquals(currentRemainingSeconds, response.getCurrentRemainingSeconds());
    }

    @Test
    @DisplayName("응답 데이터 일관성 검증")
    void shouldMaintainDataConsistency() {
        // given
        long startTime = System.currentTimeMillis();
        long durationSeconds = 600;
        long remainingSeconds = 400;
        long currentRemainingSeconds = 350;

        // when
        TimerStateResponse response = new TimerStateResponse(
            true, startTime, durationSeconds, remainingSeconds, currentRemainingSeconds
        );

        // then - 논리적 일관성 확인
        assertTrue(response.getDurationSeconds() >= response.getRemainingSeconds());
        assertTrue(response.getRemainingSeconds() >= response.getCurrentRemainingSeconds());
        assertTrue(response.getStartTime() > 0);
        assertTrue(response.getDurationSeconds() > 0);
    }

    @Test
    @DisplayName("경계값 검증")
    void shouldHandleBoundaryValues() {
        // when & then - 0값들
        TimerStateResponse zeroResponse = new TimerStateResponse(false, 0, 0, 0, 0);
        assertFalse(zeroResponse.isRunning());
        assertEquals(0, zeroResponse.getStartTime());

        // when & then - 음수는 비즈니스 로직에서 방지되지만 DTO 자체는 수용
        TimerStateResponse negativeResponse = new TimerStateResponse(false, -1, -1, -1, -1);
        assertEquals(-1, negativeResponse.getStartTime());
        assertEquals(-1, negativeResponse.getDurationSeconds());
    }
}