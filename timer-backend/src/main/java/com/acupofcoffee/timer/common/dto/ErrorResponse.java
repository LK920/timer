package com.acupofcoffee.timer.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 에러 응답을 위한 공통 DTO
 * 모든 에러 응답의 표준 형식
 */
@Schema(description = "에러 응답")
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "TIMER_001")
    private final String code;

    @Schema(description = "에러 메시지", example = "타이머가 이미 실행 중입니다")
    private final String message;

    @Schema(description = "상세 메시지 (선택사항)", example = "Duration: 300 seconds")
    private final String details;

    @Schema(description = "에러 발생 시각", example = "2023-01-01T12:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    @Schema(description = "요청 경로", example = "/api/timer/start")
    private final String path;

    public ErrorResponse(String code, String message, String details, String path) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String code, String message, String path) {
        this(code, message, null, path);
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }
}