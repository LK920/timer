package com.acupofcoffee.timer.common.exception;

import com.acupofcoffee.timer.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리를 위한 RestControllerAdvice
 * 모든 컨트롤러에서 발생하는 예외를 일관성 있게 처리
 * 커스텀 에러 추가를 고려한 확장 가능한 구조
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 타이머 비즈니스 예외 처리
     */
    @ExceptionHandler(TimerBusinessException.class)
    public ResponseEntity<ErrorResponse> handleTimerBusinessException(
            TimerBusinessException ex, HttpServletRequest request) {

        logger.warn("Timer business exception: {} at {}", ex.getMessage(), request.getRequestURI());

        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(
            errorCode.getCode(),
            errorCode.getMessage(),
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * IllegalArgumentException 처리 (주로 입력 검증 실패)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        logger.warn("Validation error: {} at {}", ex.getMessage(), request.getRequestURI());

        ErrorCode errorCode = determineValidationErrorCode(ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            errorCode.getCode(),
            errorCode.getMessage(),
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * IllegalStateException 처리 (주로 상태 전이 오류)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {

        logger.warn("State transition error: {} at {}", ex.getMessage(), request.getRequestURI());

        ErrorCode errorCode = determineStateErrorCode(ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            errorCode.getCode(),
            errorCode.getMessage(),
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * 일반적인 RuntimeException 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {

        logger.error("Runtime exception: {} at {}", ex.getMessage(), request.getRequestURI(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
            ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
            "처리 중 오류가 발생했습니다",
            request.getRequestURI()
        );

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * 예상하지 못한 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        logger.error("Unexpected exception: {} at {}", ex.getMessage(), request.getRequestURI(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
            ErrorCode.UNKNOWN_ERROR.getCode(),
            ErrorCode.UNKNOWN_ERROR.getMessage(),
            "예상치 못한 오류가 발생했습니다",
            request.getRequestURI()
        );

        return ResponseEntity
                .status(ErrorCode.UNKNOWN_ERROR.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * IllegalArgumentException 메시지를 기반으로 적절한 ErrorCode 결정
     * 메시지 패턴 매칭을 통해 더 구체적인 에러 코드 제공
     */
    private ErrorCode determineValidationErrorCode(String message) {
        if (message == null) {
            return ErrorCode.INVALID_PARAMETER;
        }

        message = message.toLowerCase();

        if (message.contains("duration must be positive") ||
            message.contains("duration") && message.contains("positive")) {
            return ErrorCode.DURATION_TOO_SHORT;
        }

        if (message.contains("duration cannot exceed 24 hours") ||
            message.contains("24") && message.contains("hour")) {
            return ErrorCode.DURATION_TOO_LONG;
        }

        if (message.contains("duration")) {
            return ErrorCode.INVALID_DURATION;
        }

        return ErrorCode.INVALID_PARAMETER;
    }

    /**
     * IllegalStateException 메시지를 기반으로 적절한 ErrorCode 결정
     */
    private ErrorCode determineStateErrorCode(String message) {
        if (message == null) {
            return ErrorCode.INTERNAL_SERVER_ERROR;
        }

        message = message.toLowerCase();

        if (message.contains("cannot start") && message.contains("already")) {
            return ErrorCode.TIMER_ALREADY_RUNNING;
        }

        if (message.contains("cannot start")) {
            return ErrorCode.TIMER_ALREADY_RUNNING;
        }

        if (message.contains("cannot pause")) {
            return ErrorCode.TIMER_CANNOT_PAUSE;
        }

        if (message.contains("cannot resume")) {
            return ErrorCode.TIMER_CANNOT_RESUME;
        }

        if (message.contains("not running")) {
            return ErrorCode.TIMER_NOT_RUNNING;
        }

        return ErrorCode.INTERNAL_SERVER_ERROR;
    }
}