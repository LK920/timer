package com.acupofcoffee.timer.presentation.timer.dto;

/**
 * 타이머 시작 요청을 위한 Presentation Layer DTO
 */
public class StartTimerRequest {

    private final long durationSeconds;

    public StartTimerRequest(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    /**
     * 요청 데이터 검증
     */
    public void validate() {
        if (durationSeconds <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        if (durationSeconds > 24 * 3600) { // 24시간 제한
            throw new IllegalArgumentException("Duration cannot exceed 24 hours");
        }
    }
}