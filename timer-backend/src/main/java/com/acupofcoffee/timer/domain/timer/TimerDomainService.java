package com.acupofcoffee.timer.domain.timer;

import org.springframework.stereotype.Service;

/**
 * Timer 도메인 서비스
 * 복잡한 도메인 로직이나 여러 엔티티 간의 상호작용을 담당
 */
@Service
public class TimerDomainService {

    /**
     * 타이머 시작 가능 여부 검증
     */
    public boolean canStartTimer(Timer timer, Duration requestedDuration) {
        if (timer.getStatus() == TimerStatus.RUNNING) {
            return false;
        }

        if (requestedDuration == null || requestedDuration.isZero()) {
            return false;
        }

        // 최대 시간 제한 (예: 24시간)
        if (requestedDuration.getSeconds() > 24 * 3600) {
            return false;
        }

        return true;
    }

    /**
     * 타이머 일시정지 가능 여부 검증
     */
    public boolean canPauseTimer(Timer timer) {
        return timer.getStatus() == TimerStatus.RUNNING;
    }

    /**
     * 타이머 재개 가능 여부 검증
     */
    public boolean canResumeTimer(Timer timer) {
        return timer.getStatus() == TimerStatus.PAUSED &&
               timer.getRemainingTime() != null &&
               timer.getRemainingTime().isPositive();
    }

    /**
     * 타이머 완료 처리
     */
    public void completeTimer(Timer timer) {
        if (timer.isCompleted()) {
            // 타이머 완료 시 추가 비즈니스 로직
            // 예: 알림 발송, 로그 기록 등
            timer.reset(); // 자동으로 초기화
        }
    }

    /**
     * 타이머 상태 검증
     */
    public void validateTimerState(Timer timer) {
        if (timer == null) {
            throw new IllegalArgumentException("Timer cannot be null");
        }

        if (timer.getId() == null) {
            throw new IllegalArgumentException("Timer ID cannot be null");
        }

        // 타이머가 완료된 경우 자동 처리
        if (timer.isCompleted()) {
            completeTimer(timer);
        }
    }
}