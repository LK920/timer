package com.acupofcoffee.timer.application.timer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class StartTimerCommandTest {

    @Test
    @DisplayName("유효한 duration으로 Command 생성")
    void shouldCreateCommandWithValidDuration() {
        // given
        long validDuration = 300; // 5분

        // when
        StartTimerCommand command = new StartTimerCommand(validDuration);

        // then
        assertEquals(validDuration, command.getDurationSeconds());
    }

    @Test
    @DisplayName("0 이하의 duration으로 Command 생성 시 예외 발생")
    void shouldThrowExceptionWhenCreateWithZeroDuration() {
        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new StartTimerCommand(0)
        );
        assertEquals("Duration must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("음수 duration으로 Command 생성 시 예외 발생")
    void shouldThrowExceptionWhenCreateWithNegativeDuration() {
        // given
        long negativeDuration = -60;

        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new StartTimerCommand(negativeDuration)
        );
        assertEquals("Duration must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("최소 유효값으로 Command 생성")
    void shouldCreateCommandWithMinimumValidDuration() {
        // given
        long minimumDuration = 1;

        // when
        StartTimerCommand command = new StartTimerCommand(minimumDuration);

        // then
        assertEquals(minimumDuration, command.getDurationSeconds());
    }

    @Test
    @DisplayName("큰 값으로 Command 생성")
    void shouldCreateCommandWithLargeDuration() {
        // given
        long largeDuration = 86400; // 24시간

        // when
        StartTimerCommand command = new StartTimerCommand(largeDuration);

        // then
        assertEquals(largeDuration, command.getDurationSeconds());
    }

    @Test
    @DisplayName("Command 객체 불변성 확인")
    void shouldBeImmutable() {
        // given
        long duration = 300;
        StartTimerCommand command = new StartTimerCommand(duration);

        // when
        long retrievedDuration = command.getDurationSeconds();

        // then
        assertEquals(duration, retrievedDuration);
        // getDurationSeconds를 여러 번 호출해도 같은 값
        assertEquals(retrievedDuration, command.getDurationSeconds());
    }
}