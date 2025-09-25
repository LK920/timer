package com.acupofcoffee.timer.presentation.timer.dto;

/**
 * 타이머 상태 응답을 위한 Presentation Layer DTO
 * 외부 API 응답 전용
 */
public class TimerStateResponse {

    private final boolean running;
    private final long startTime;
    private final long durationSeconds;
    private final long remainingSeconds;
    private final long currentRemainingSeconds;

    public TimerStateResponse(boolean running, long startTime, long durationSeconds,
                             long remainingSeconds, long currentRemainingSeconds) {
        this.running = running;
        this.startTime = startTime;
        this.durationSeconds = durationSeconds;
        this.remainingSeconds = remainingSeconds;
        this.currentRemainingSeconds = currentRemainingSeconds;
    }

    // Getters
    public boolean isRunning() {
        return running;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public long getRemainingSeconds() {
        return remainingSeconds;
    }

    public long getCurrentRemainingSeconds() {
        return currentRemainingSeconds;
    }
}