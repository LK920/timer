package com.acupofcoffee.timer.presentation.timer.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class StartTimerRequestTest {

    @Test
    @DisplayName("유효한 duration으로 요청 생성")
    void shouldCreateRequestWithValidDuration() {
        // given
        long validDuration = 300; // 5분

        // when
        StartTimerRequest request = new StartTimerRequest(validDuration);

        // then
        assertEquals(validDuration, request.getDurationSeconds());
    }

    @Test
    @DisplayName("최소 유효값으로 요청 생성")
    void shouldCreateRequestWithMinimumValidDuration() {
        // given
        long minimumDuration = 1;

        // when
        StartTimerRequest request = new StartTimerRequest(minimumDuration);

        // then
        assertEquals(minimumDuration, request.getDurationSeconds());
    }

    @Test
    @DisplayName("최대 유효값으로 요청 생성")
    void shouldCreateRequestWithMaximumValidDuration() {
        // given
        long maximumDuration = 24 * 3600; // 24시간

        // when
        StartTimerRequest request = new StartTimerRequest(maximumDuration);

        // then
        assertEquals(maximumDuration, request.getDurationSeconds());
    }

    @Test
    @DisplayName("유효한 요청 검증 성공")
    void shouldPassValidationWithValidDuration() {
        // given
        StartTimerRequest validRequest = new StartTimerRequest(300);

        // when & then
        assertDoesNotThrow(validRequest::validate);
    }

    @Test
    @DisplayName("최소값 요청 검증 성공")
    void shouldPassValidationWithMinimumDuration() {
        // given
        StartTimerRequest minRequest = new StartTimerRequest(1);

        // when & then
        assertDoesNotThrow(minRequest::validate);
    }

    @Test
    @DisplayName("최대값 요청 검증 성공")
    void shouldPassValidationWithMaximumDuration() {
        // given
        StartTimerRequest maxRequest = new StartTimerRequest(24 * 3600);

        // when & then
        assertDoesNotThrow(maxRequest::validate);
    }

    @Test
    @DisplayName("0 duration 요청 검증 실패")
    void shouldFailValidationWithZeroDuration() {
        // given
        StartTimerRequest invalidRequest = new StartTimerRequest(0);

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            invalidRequest::validate
        );
        assertEquals("Duration must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("음수 duration 요청 검증 실패")
    void shouldFailValidationWithNegativeDuration() {
        // given
        StartTimerRequest invalidRequest = new StartTimerRequest(-60);

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            invalidRequest::validate
        );
        assertEquals("Duration must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("24시간 초과 duration 요청 검증 실패")
    void shouldFailValidationWithExcessiveDuration() {
        // given
        long excessiveDuration = 25 * 3600; // 25시간
        StartTimerRequest invalidRequest = new StartTimerRequest(excessiveDuration);

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            invalidRequest::validate
        );
        assertEquals("Duration cannot exceed 24 hours", exception.getMessage());
    }

    @Test
    @DisplayName("24시간 + 1초 duration 요청 검증 실패")
    void shouldFailValidationWithSlightlyExcessiveDuration() {
        // given
        long slightlyExcessiveDuration = 24 * 3600 + 1; // 24시간 1초
        StartTimerRequest invalidRequest = new StartTimerRequest(slightlyExcessiveDuration);

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            invalidRequest::validate
        );
        assertEquals("Duration cannot exceed 24 hours", exception.getMessage());
    }

    @Test
    @DisplayName("요청 객체 불변성 확인")
    void shouldBeImmutable() {
        // given
        long duration = 300;
        StartTimerRequest request = new StartTimerRequest(duration);

        // when
        long retrievedDuration = request.getDurationSeconds();

        // then
        assertEquals(duration, retrievedDuration);
        // 여러 번 호출해도 같은 값
        assertEquals(retrievedDuration, request.getDurationSeconds());
    }

    @Test
    @DisplayName("다양한 유효 범위 값 테스트")
    void shouldHandleVariousValidValues() {
        // given & when & then
        long[] validDurations = {1, 60, 300, 1800, 3600, 7200, 86400};

        for (long duration : validDurations) {
            StartTimerRequest request = new StartTimerRequest(duration);
            assertEquals(duration, request.getDurationSeconds());
            assertDoesNotThrow(request::validate);
        }
    }

    @Test
    @DisplayName("다양한 무효 범위 값 테스트")
    void shouldRejectVariousInvalidValues() {
        // given
        long[] invalidDurations = {-1, 0, -60, 86401, 90000, Long.MAX_VALUE};

        for (long duration : invalidDurations) {
            // when
            StartTimerRequest request = new StartTimerRequest(duration);

            // then
            assertEquals(duration, request.getDurationSeconds());
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                request::validate,
                "Duration " + duration + " should be invalid"
            );
            assertTrue(
                exception.getMessage().contains("Duration must be positive") ||
                exception.getMessage().contains("Duration cannot exceed 24 hours")
            );
        }
    }

    @Test
    @DisplayName("경계값 정확성 테스트")
    void shouldHandleBoundaryValuesCorrectly() {
        // given - 경계값 바로 아래
        StartTimerRequest belowMax = new StartTimerRequest(24 * 3600 - 1);
        assertDoesNotThrow(belowMax::validate);

        // given - 경계값 정확히
        StartTimerRequest exactMax = new StartTimerRequest(24 * 3600);
        assertDoesNotThrow(exactMax::validate);

        // given - 경계값 바로 위
        StartTimerRequest aboveMax = new StartTimerRequest(24 * 3600 + 1);
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            aboveMax::validate
        );
        assertEquals("Duration cannot exceed 24 hours", exception.getMessage());
    }

    @Test
    @DisplayName("검증 메소드 여러 번 호출 테스트")
    void shouldMaintainValidationConsistency() {
        // given
        StartTimerRequest validRequest = new StartTimerRequest(300);
        StartTimerRequest invalidRequest = new StartTimerRequest(-1);

        // when & then - 여러 번 호출해도 같은 결과
        assertDoesNotThrow(validRequest::validate);
        assertDoesNotThrow(validRequest::validate);
        assertDoesNotThrow(validRequest::validate);

        assertThrows(IllegalArgumentException.class, invalidRequest::validate);
        assertThrows(IllegalArgumentException.class, invalidRequest::validate);
        assertThrows(IllegalArgumentException.class, invalidRequest::validate);
    }
}