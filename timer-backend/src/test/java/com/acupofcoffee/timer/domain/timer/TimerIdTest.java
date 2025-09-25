package com.acupofcoffee.timer.domain.timer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class TimerIdTest {

    @Test
    @DisplayName("TimerId 생성 성공")
    void shouldGenerateTimerId() {
        // when
        TimerId timerId = TimerId.generate();

        // then
        assertNotNull(timerId);
        assertNotNull(timerId.getValue());
        assertFalse(timerId.getValue().isEmpty());
    }

    @Test
    @DisplayName("TimerId.of()로 생성 성공")
    void shouldCreateTimerIdFromValue() {
        // given
        String value = "test-timer-id";

        // when
        TimerId timerId = TimerId.of(value);

        // then
        assertEquals(value, timerId.getValue());
    }

    @Test
    @DisplayName("null 값으로 TimerId 생성 시 예외 발생")
    void shouldThrowExceptionWhenCreateWithNull() {
        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimerId.of(null)
        );
        assertEquals("TimerId cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("빈 문자열로 TimerId 생성 시 예외 발생")
    void shouldThrowExceptionWhenCreateWithEmptyString() {
        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimerId.of("")
        );
        assertEquals("TimerId cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("공백 문자열로 TimerId 생성 시 예외 발생")
    void shouldThrowExceptionWhenCreateWithWhitespace() {
        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TimerId.of("   ")
        );
        assertEquals("TimerId cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("동일한 값의 TimerId 비교 시 동등함")
    void shouldBeEqualWithSameValue() {
        // given
        String value = "same-id";
        TimerId timerId1 = TimerId.of(value);
        TimerId timerId2 = TimerId.of(value);

        // when & then
        assertEquals(timerId1, timerId2);
        assertEquals(timerId1.hashCode(), timerId2.hashCode());
    }

    @Test
    @DisplayName("다른 값의 TimerId 비교 시 다름")
    void shouldNotBeEqualWithDifferentValue() {
        // given
        TimerId timerId1 = TimerId.of("id1");
        TimerId timerId2 = TimerId.of("id2");

        // when & then
        assertNotEquals(timerId1, timerId2);
        assertNotEquals(timerId1.hashCode(), timerId2.hashCode());
    }

    @Test
    @DisplayName("자기 자신과 비교 시 동등함")
    void shouldBeEqualWithSelf() {
        // given
        TimerId timerId = TimerId.of("test-id");

        // when & then
        assertEquals(timerId, timerId);
    }

    @Test
    @DisplayName("null과 비교 시 다름")
    void shouldNotBeEqualWithNull() {
        // given
        TimerId timerId = TimerId.of("test-id");

        // when & then
        assertNotEquals(timerId, null);
    }

    @Test
    @DisplayName("다른 타입 객체와 비교 시 다름")
    void shouldNotBeEqualWithDifferentType() {
        // given
        TimerId timerId = TimerId.of("test-id");
        String string = "test-id";

        // when & then
        assertNotEquals(timerId, string);
    }

    @Test
    @DisplayName("toString() 메소드 테스트")
    void shouldReturnProperToString() {
        // given
        String value = "test-id";
        TimerId timerId = TimerId.of(value);

        // when
        String toString = timerId.toString();

        // then
        assertTrue(toString.contains("TimerId"));
        assertTrue(toString.contains(value));
    }

    @Test
    @DisplayName("생성된 TimerId들은 서로 다른 값을 가짐")
    void shouldGenerateDifferentIds() {
        // when
        TimerId id1 = TimerId.generate();
        TimerId id2 = TimerId.generate();

        // then
        assertNotEquals(id1, id2);
        assertNotEquals(id1.getValue(), id2.getValue());
    }
}