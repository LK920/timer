package com.acupofcoffee.timer.domain.timer;

/**
 * Timer 상태를 나타내는 열거형
 */
public enum TimerStatus {
    STOPPED,    // 정지 상태
    RUNNING,    // 실행 중
    PAUSED,     // 일시정지
    COMPLETED   // 완료
}