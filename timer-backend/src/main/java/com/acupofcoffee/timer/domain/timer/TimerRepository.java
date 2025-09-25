package com.acupofcoffee.timer.domain.timer;

import java.util.Optional;

/**
 * Timer 도메인을 위한 Repository 인터페이스
 */
public interface TimerRepository {

    /**
     * Timer 저장
     */
    void save(Timer timer);

    /**
     * Timer ID로 조회
     */
    Optional<Timer> findById(TimerId timerId);

    /**
     * 현재 활성화된 Timer 조회 (단일 타이머 애플리케이션이므로)
     */
    Optional<Timer> findActiveTimer();

    /**
     * Timer 삭제
     */
    void delete(TimerId timerId);

    /**
     * 모든 Timer 삭제
     */
    void deleteAll();
}