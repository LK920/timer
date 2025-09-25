package com.acupofcoffee.timer.domain.timer;

import java.time.Instant;

/**
 * Timer 도메인 엔티티
 * 타이머의 핵심 비즈니스 로직을 담당
 */
public class Timer {

    private final TimerId id;
    private TimerStatus status;
    private Duration duration;
    private Instant startedAt;
    private Duration remainingTime;

    public Timer(TimerId id) {
        this.id = id;
        this.status = TimerStatus.STOPPED;
        this.duration = Duration.ZERO;
        this.remainingTime = Duration.ZERO;
    }

    /**
     * 타이머 시작
     */
    public void start(Duration duration) {
        if (this.status == TimerStatus.RUNNING) {
            throw new IllegalStateException("Timer is already running");
        }

        this.duration = duration;
        this.remainingTime = duration;
        this.status = TimerStatus.RUNNING;
        this.startedAt = Instant.now();
    }

    /**
     * 타이머 일시정지
     */
    public void pause() {
        if (this.status != TimerStatus.RUNNING) {
            throw new IllegalStateException("Timer is not running");
        }

        long elapsedSeconds = Instant.now().getEpochSecond() - this.startedAt.getEpochSecond();
        this.remainingTime = Duration.of(Math.max(0, this.duration.getSeconds() - elapsedSeconds));
        this.status = TimerStatus.PAUSED;
    }

    /**
     * 타이머 재개
     */
    public void resume() {
        if (this.status != TimerStatus.PAUSED || this.remainingTime.isZero()) {
            throw new IllegalStateException("Cannot resume timer in current state");
        }

        this.duration = this.remainingTime;
        this.status = TimerStatus.RUNNING;
        this.startedAt = Instant.now();
        this.remainingTime = this.duration;
    }

    /**
     * 타이머 초기화
     */
    public void reset() {
        this.status = TimerStatus.STOPPED;
        this.duration = Duration.ZERO;
        this.remainingTime = Duration.ZERO;
        this.startedAt = null;
    }

    /**
     * 현재 남은 시간 계산
     */
    public Duration getCurrentRemainingTime() {
        if (this.status == TimerStatus.RUNNING && this.startedAt != null) {
            long elapsedSeconds = Instant.now().getEpochSecond() - this.startedAt.getEpochSecond();
            long remaining = Math.max(0, this.duration.getSeconds() - elapsedSeconds);
            return Duration.of(remaining);
        } else if (this.status == TimerStatus.PAUSED) {
            return this.remainingTime;
        }
        return Duration.ZERO;
    }

    /**
     * 타이머 완료 여부 확인
     */
    public boolean isCompleted() {
        return this.status == TimerStatus.RUNNING && getCurrentRemainingTime().isZero();
    }

    // Getters
    public TimerId getId() {
        return id;
    }

    public TimerStatus getStatus() {
        return status;
    }

    public Duration getDuration() {
        return duration;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Duration getRemainingTime() {
        return remainingTime;
    }
}