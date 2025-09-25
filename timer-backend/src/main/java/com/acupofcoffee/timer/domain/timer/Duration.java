package com.acupofcoffee.timer.domain.timer;

import java.util.Objects;

/**
 * 시간 지속시간을 나타내는 Value Object
 */
public class Duration {

    public static final Duration ZERO = new Duration(0);

    private final long seconds;

    private Duration(long seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
        this.seconds = seconds;
    }

    public static Duration of(long seconds) {
        return new Duration(seconds);
    }

    public static Duration ofMinutes(long minutes) {
        return new Duration(minutes * 60);
    }

    public static Duration ofHours(long hours) {
        return new Duration(hours * 3600);
    }

    public long getSeconds() {
        return seconds;
    }

    public long getMinutes() {
        return seconds / 60;
    }

    public long getHours() {
        return seconds / 3600;
    }

    public boolean isZero() {
        return seconds == 0;
    }

    public boolean isPositive() {
        return seconds > 0;
    }

    public Duration minus(Duration other) {
        return Duration.of(Math.max(0, this.seconds - other.seconds));
    }

    public Duration plus(Duration other) {
        return Duration.of(this.seconds + other.seconds);
    }

    /**
     * HH:MM:SS 형식으로 포맷팅
     */
    public String format() {
        long hours = getHours();
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Duration duration = (Duration) o;
        return seconds == duration.seconds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(seconds);
    }

    @Override
    public String toString() {
        return "Duration{" + "seconds=" + seconds + '}';
    }
}