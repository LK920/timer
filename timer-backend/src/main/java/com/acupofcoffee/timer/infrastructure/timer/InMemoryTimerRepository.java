package com.acupofcoffee.timer.infrastructure.timer;

import com.acupofcoffee.timer.domain.timer.Timer;
import com.acupofcoffee.timer.domain.timer.TimerId;
import com.acupofcoffee.timer.domain.timer.TimerRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 인메모리 Timer Repository 구현
 * 단순한 애플리케이션이므로 메모리 저장소 사용
 */
@Repository
public class InMemoryTimerRepository implements TimerRepository {

    private final Map<String, Timer> timers = new ConcurrentHashMap<>();

    @Override
    public void save(Timer timer) {
        if (timer == null || timer.getId() == null) {
            throw new IllegalArgumentException("Timer and TimerId cannot be null");
        }
        timers.put(timer.getId().getValue(), timer);
    }

    @Override
    public Optional<Timer> findById(TimerId timerId) {
        if (timerId == null) {
            throw new IllegalArgumentException("TimerId cannot be null");
        }
        return Optional.ofNullable(timers.get(timerId.getValue()));
    }

    @Override
    public Optional<Timer> findActiveTimer() {
        // 단일 타이머 애플리케이션이므로 첫 번째 타이머 반환
        return timers.values().stream().findFirst();
    }

    @Override
    public void delete(TimerId timerId) {
        if (timerId == null) {
            throw new IllegalArgumentException("TimerId cannot be null");
        }
        timers.remove(timerId.getValue());
    }

    @Override
    public void deleteAll() {
        timers.clear();
    }
}