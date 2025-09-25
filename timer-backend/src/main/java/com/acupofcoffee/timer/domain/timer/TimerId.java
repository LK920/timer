package com.acupofcoffee.timer.domain.timer;

import java.util.Objects;
import java.util.UUID;

/**
 * Timer 식별자 Value Object
 */
public class TimerId {

    private final String value;

    private TimerId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("TimerId cannot be null or empty");
        }
        this.value = value;
    }

    public static TimerId generate() {
        return new TimerId(UUID.randomUUID().toString());
    }

    public static TimerId of(String value) {
        return new TimerId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimerId timerId = (TimerId) o;
        return Objects.equals(value, timerId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "TimerId{" + "value='" + value + '\'' + '}';
    }
}