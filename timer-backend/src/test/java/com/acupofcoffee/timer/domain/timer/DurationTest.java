package com.acupofcoffee.timer.domain.timer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class DurationTest {

    @Test
    @DisplayName("Duration 생성 - 초 단위")
    void shouldCreateDurationWithSeconds() {
        // given
        long seconds = 120;

        // when
        Duration duration = Duration.of(seconds);

        // then
        assertEquals(seconds, duration.getSeconds());
        assertEquals(2, duration.getMinutes());
        assertEquals(0, duration.getHours());
    }

    @Test
    @DisplayName("Duration 생성 - 분 단위")
    void shouldCreateDurationWithMinutes() {
        // given
        long minutes = 5;

        // when
        Duration duration = Duration.ofMinutes(minutes);

        // then
        assertEquals(300, duration.getSeconds());
        assertEquals(minutes, duration.getMinutes());
        assertEquals(0, duration.getHours());
    }

    @Test
    @DisplayName("Duration 생성 - 시간 단위")
    void shouldCreateDurationWithHours() {
        // given
        long hours = 2;

        // when
        Duration duration = Duration.ofHours(hours);

        // then
        assertEquals(7200, duration.getSeconds());
        assertEquals(120, duration.getMinutes());
        assertEquals(hours, duration.getHours());
    }

    @Test
    @DisplayName("음수 Duration 생성 시 예외 발생")
    void shouldThrowExceptionWhenCreateWithNegativeValue() {
        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Duration.of(-1)
        );
        assertEquals("Duration cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("ZERO Duration 상수 확인")
    void shouldHaveZeroConstant() {
        // given
        Duration zero = Duration.ZERO;

        // then
        assertEquals(0, zero.getSeconds());
        assertTrue(zero.isZero());
        assertFalse(zero.isPositive());
    }

    @Test
    @DisplayName("Duration의 isZero() 메소드")
    void shouldDetectZeroDuration() {
        // given
        Duration zero = Duration.of(0);
        Duration nonZero = Duration.of(10);

        // then
        assertTrue(zero.isZero());
        assertFalse(nonZero.isZero());
    }

    @Test
    @DisplayName("Duration의 isPositive() 메소드")
    void shouldDetectPositiveDuration() {
        // given
        Duration zero = Duration.of(0);
        Duration positive = Duration.of(10);

        // then
        assertFalse(zero.isPositive());
        assertTrue(positive.isPositive());
    }

    @Test
    @DisplayName("Duration 빼기 연산")
    void shouldSubtractDuration() {
        // given
        Duration duration1 = Duration.of(100);
        Duration duration2 = Duration.of(30);

        // when
        Duration result = duration1.minus(duration2);

        // then
        assertEquals(70, result.getSeconds());
    }

    @Test
    @DisplayName("Duration 빼기 연산 - 음수 결과일 때 0 반환")
    void shouldReturnZeroWhenSubtractionResultIsNegative() {
        // given
        Duration duration1 = Duration.of(30);
        Duration duration2 = Duration.of(100);

        // when
        Duration result = duration1.minus(duration2);

        // then
        assertEquals(0, result.getSeconds());
        assertTrue(result.isZero());
    }

    @Test
    @DisplayName("Duration 더하기 연산")
    void shouldAddDuration() {
        // given
        Duration duration1 = Duration.of(60);
        Duration duration2 = Duration.of(30);

        // when
        Duration result = duration1.plus(duration2);

        // then
        assertEquals(90, result.getSeconds());
    }

    @Test
    @DisplayName("Duration 포맷팅 - HH:MM:SS")
    void shouldFormatDuration() {
        // given
        Duration duration = Duration.of(3661); // 1시간 1분 1초

        // when
        String formatted = duration.format();

        // then
        assertEquals("01:01:01", formatted);
    }

    @Test
    @DisplayName("Duration 포맷팅 - 0초")
    void shouldFormatZeroDuration() {
        // given
        Duration duration = Duration.ZERO;

        // when
        String formatted = duration.format();

        // then
        assertEquals("00:00:00", formatted);
    }

    @Test
    @DisplayName("Duration 포맷팅 - 큰 시간")
    void shouldFormatLargeDuration() {
        // given
        Duration duration = Duration.of(90061); // 25시간 1분 1초

        // when
        String formatted = duration.format();

        // then
        assertEquals("25:01:01", formatted);
    }

    @Test
    @DisplayName("분 단위 계산 확인")
    void shouldCalculateMinutesCorrectly() {
        // given
        Duration duration = Duration.of(150); // 2분 30초

        // when & then
        assertEquals(2, duration.getMinutes());
        assertEquals(150, duration.getSeconds());
    }

    @Test
    @DisplayName("시간 단위 계산 확인")
    void shouldCalculateHoursCorrectly() {
        // given
        Duration duration = Duration.of(3900); // 1시간 5분

        // when & then
        assertEquals(1, duration.getHours());
        assertEquals(65, duration.getMinutes());
        assertEquals(3900, duration.getSeconds());
    }

    @Test
    @DisplayName("Duration 동등성 비교")
    void shouldBeEqualWithSameSeconds() {
        // given
        Duration duration1 = Duration.of(60);
        Duration duration2 = Duration.of(60);
        Duration duration3 = Duration.of(30);

        // then
        assertEquals(duration1, duration2);
        assertEquals(duration1.hashCode(), duration2.hashCode());
        assertNotEquals(duration1, duration3);
    }

    @Test
    @DisplayName("Duration toString() 메소드")
    void shouldReturnProperToString() {
        // given
        Duration duration = Duration.of(60);

        // when
        String toString = duration.toString();

        // then
        assertTrue(toString.contains("Duration"));
        assertTrue(toString.contains("60"));
    }

    @Test
    @DisplayName("Duration과 null 비교")
    void shouldNotBeEqualWithNull() {
        // given
        Duration duration = Duration.of(60);

        // when & then
        assertNotEquals(duration, null);
    }

    @Test
    @DisplayName("Duration과 다른 타입 객체 비교")
    void shouldNotBeEqualWithDifferentType() {
        // given
        Duration duration = Duration.of(60);
        Long longValue = 60L;

        // when & then
        assertNotEquals(duration, longValue);
    }
}