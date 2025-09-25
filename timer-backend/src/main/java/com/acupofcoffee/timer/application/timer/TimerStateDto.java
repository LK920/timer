package com.acupofcoffee.timer.application.timer;

import com.acupofcoffee.timer.domain.timer.Timer;
import com.acupofcoffee.timer.domain.timer.TimerStatus;

/**
 * Timer 상태를 외부로 전달하기 위한 DTO
 */
public class TimerStateDto {

    private final boolean running;
    private final long startTime;
    private final long durationSeconds;
    private final long remainingSeconds;
    private final long currentRemainingSeconds;

    private TimerStateDto(boolean running, long startTime, long durationSeconds,
                         long remainingSeconds, long currentRemainingSeconds) {
        this.running = running;
        this.startTime = startTime;
        this.durationSeconds = durationSeconds;
        this.remainingSeconds = remainingSeconds;
        this.currentRemainingSeconds = currentRemainingSeconds;
    }

    public static TimerStateDto from(Timer timer) {
        boolean running = timer.getStatus() == TimerStatus.RUNNING;
        long startTime = timer.getStartedAt() != null ? timer.getStartedAt().getEpochSecond() : 0;
        long durationSeconds = timer.getDuration() != null ? timer.getDuration().getSeconds() : 0;
        long remainingSeconds = timer.getRemainingTime() != null ? timer.getRemainingTime().getSeconds() : 0;
        long currentRemainingSeconds = timer.getCurrentRemainingTime().getSeconds();

        return new TimerStateDto(running, startTime, durationSeconds, remainingSeconds, currentRemainingSeconds);
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