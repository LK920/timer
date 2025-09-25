package com.acupofcoffee.timer.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 에러 코드 정의
 * 커스텀 에러 추가를 고려한 확장 가능한 구조
 */
public enum ErrorCode {

    // 타이머 관련 에러 (TIMER_XXX)
    TIMER_ALREADY_RUNNING(HttpStatus.CONFLICT, "TIMER_001", "타이머가 이미 실행 중입니다"),
    TIMER_NOT_RUNNING(HttpStatus.CONFLICT, "TIMER_002", "타이머가 실행 중이 아닙니다"),
    TIMER_CANNOT_PAUSE(HttpStatus.CONFLICT, "TIMER_003", "타이머를 일시정지할 수 없는 상태입니다"),
    TIMER_CANNOT_RESUME(HttpStatus.CONFLICT, "TIMER_004", "타이머를 재개할 수 없는 상태입니다"),
    TIMER_NOT_FOUND(HttpStatus.NOT_FOUND, "TIMER_005", "타이머를 찾을 수 없습니다"),

    // 입력 검증 에러 (VALIDATION_XXX)
    INVALID_DURATION(HttpStatus.BAD_REQUEST, "VALIDATION_001", "잘못된 시간 설정입니다"),
    DURATION_TOO_SHORT(HttpStatus.BAD_REQUEST, "VALIDATION_002", "타이머 시간이 너무 짧습니다"),
    DURATION_TOO_LONG(HttpStatus.BAD_REQUEST, "VALIDATION_003", "타이머 시간이 너무 깁니다 (최대 24시간)"),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "VALIDATION_004", "잘못된 파라미터입니다"),

    // 시스템 에러 (SYSTEM_XXX)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_001", "서버 내부 오류가 발생했습니다"),
    DATA_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_002", "데이터 접근 중 오류가 발생했습니다"),

    // 일반 에러
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN_001", "알 수 없는 오류가 발생했습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}