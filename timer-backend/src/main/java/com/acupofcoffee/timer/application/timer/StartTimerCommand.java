package com.acupofcoffee.timer.application.timer;

/**
 * 타이머 시작을 위한 커맨드 객체
 */
public class StartTimerCommand {

    private final long durationSeconds;

    public StartTimerCommand(long durationSeconds) {
        if (durationSeconds <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        this.durationSeconds = durationSeconds;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }
}