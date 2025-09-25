package com.acupofcoffee.timer.common.exception;

/**
 * 타이머 비즈니스 로직 예외
 * 도메인 규칙 위반 시 발생하는 커스텀 예외
 */
public class TimerBusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public TimerBusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public TimerBusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public TimerBusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}