package com.acupofcoffee.timer.application.timer;

import com.acupofcoffee.timer.domain.timer.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Timer 애플리케이션 서비스
 * Use Case를 담당하며 도메인 로직을 조율
 */
@Service
@Transactional
public class TimerApplicationService {

    private final TimerRepository timerRepository;
    private final TimerDomainService timerDomainService;

    // 단일 타이머 애플리케이션을 위한 고정 ID
    private static final TimerId DEFAULT_TIMER_ID = TimerId.of("default-timer");

    public TimerApplicationService(TimerRepository timerRepository, TimerDomainService timerDomainService) {
        this.timerRepository = timerRepository;
        this.timerDomainService = timerDomainService;
    }

    /**
     * 현재 타이머 상태 조회
     */
    @Transactional(readOnly = true)
    public TimerStateDto getCurrentTimerState() {
        Timer timer = getOrCreateDefaultTimer();
        timerDomainService.validateTimerState(timer);
        return TimerStateDto.from(timer);
    }

    /**
     * 타이머 시작
     */
    public TimerStateDto startTimer(StartTimerCommand command) {
        Timer timer = getOrCreateDefaultTimer();
        Duration duration = Duration.of(command.getDurationSeconds());

        if (!timerDomainService.canStartTimer(timer, duration)) {
            throw new IllegalStateException("Cannot start timer in current state");
        }

        timer.start(duration);
        timerRepository.save(timer);

        return TimerStateDto.from(timer);
    }

    /**
     * 타이머 일시정지
     */
    public TimerStateDto pauseTimer() {
        Timer timer = getOrCreateDefaultTimer();

        if (!timerDomainService.canPauseTimer(timer)) {
            throw new IllegalStateException("Cannot pause timer in current state");
        }

        timer.pause();
        timerRepository.save(timer);

        return TimerStateDto.from(timer);
    }

    /**
     * 타이머 재개
     */
    public TimerStateDto resumeTimer() {
        Timer timer = getOrCreateDefaultTimer();

        if (!timerDomainService.canResumeTimer(timer)) {
            throw new IllegalStateException("Cannot resume timer in current state");
        }

        timer.resume();
        timerRepository.save(timer);

        return TimerStateDto.from(timer);
    }

    /**
     * 타이머 초기화
     */
    public TimerStateDto resetTimer() {
        Timer timer = getOrCreateDefaultTimer();
        timer.reset();
        timerRepository.save(timer);

        return TimerStateDto.from(timer);
    }

    /**
     * 기본 타이머 조회 또는 생성
     */
    private Timer getOrCreateDefaultTimer() {
        return timerRepository.findById(DEFAULT_TIMER_ID)
                .orElseGet(() -> {
                    Timer newTimer = new Timer(DEFAULT_TIMER_ID);
                    timerRepository.save(newTimer);
                    return newTimer;
                });
    }
}