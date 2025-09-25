package com.acupofcoffee.timer.common.exception;

import com.acupofcoffee.timer.common.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(GlobalExceptionHandlerTest.TestController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("TimerBusinessException 처리")
    void shouldHandleTimerBusinessException() throws Exception {
        mockMvc.perform(get("/test/timer-business-exception"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("TIMER_001")))
                .andExpect(jsonPath("$.message", is("타이머가 이미 실행 중입니다")))
                .andExpect(jsonPath("$.path", is("/test/timer-business-exception")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("IllegalArgumentException 처리 - Duration 검증")
    void shouldHandleIllegalArgumentExceptionForDuration() throws Exception {
        mockMvc.perform(get("/test/illegal-argument-duration"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("VALIDATION_002")))
                .andExpect(jsonPath("$.message", is("타이머 시간이 너무 짧습니다")))
                .andExpect(jsonPath("$.details", is("Duration must be positive")))
                .andExpect(jsonPath("$.path", is("/test/illegal-argument-duration")));
    }

    @Test
    @DisplayName("IllegalArgumentException 처리 - 24시간 초과")
    void shouldHandleIllegalArgumentExceptionForExcessiveDuration() throws Exception {
        mockMvc.perform(get("/test/illegal-argument-excessive"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("VALIDATION_003")))
                .andExpect(jsonPath("$.message", is("타이머 시간이 너무 깁니다 (최대 24시간)")))
                .andExpect(jsonPath("$.details", is("Duration cannot exceed 24 hours")));
    }

    @Test
    @DisplayName("IllegalStateException 처리 - 타이머 시작 불가")
    void shouldHandleIllegalStateExceptionForCannotStart() throws Exception {
        mockMvc.perform(get("/test/illegal-state-start"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("TIMER_001")))
                .andExpect(jsonPath("$.message", is("타이머가 이미 실행 중입니다")))
                .andExpect(jsonPath("$.details", is("Cannot start timer in current state")));
    }

    @Test
    @DisplayName("IllegalStateException 처리 - 일시정지 불가")
    void shouldHandleIllegalStateExceptionForCannotPause() throws Exception {
        mockMvc.perform(get("/test/illegal-state-pause"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("TIMER_003")))
                .andExpect(jsonPath("$.message", is("타이머를 일시정지할 수 없는 상태입니다")));
    }

    @Test
    @DisplayName("RuntimeException 처리")
    void shouldHandleRuntimeException() throws Exception {
        mockMvc.perform(get("/test/runtime-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("SYSTEM_001")))
                .andExpect(jsonPath("$.message", is("서버 내부 오류가 발생했습니다")))
                .andExpect(jsonPath("$.details", is("처리 중 오류가 발생했습니다")));
    }

    @Test
    @DisplayName("일반 Exception 처리")
    void shouldHandleGenericException() throws Exception {
        mockMvc.perform(get("/test/generic-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("UNKNOWN_001")))
                .andExpect(jsonPath("$.message", is("알 수 없는 오류가 발생했습니다")))
                .andExpect(jsonPath("$.details", is("예상치 못한 오류가 발생했습니다")));
    }

    @Test
    @DisplayName("에러 응답 JSON 구조 검증")
    void shouldReturnCorrectErrorResponseStructure() throws Exception {
        mockMvc.perform(get("/test/timer-business-exception"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.code").isString())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.path").isString());
    }

    /**
     * 테스트용 컨트롤러
     * 다양한 예외 상황을 시뮬레이션하기 위한 엔드포인트 제공
     */
    @RestController
    @RequestMapping("/test")
    static class TestController {

        @GetMapping("/timer-business-exception")
        public void throwTimerBusinessException() {
            throw new TimerBusinessException(ErrorCode.TIMER_ALREADY_RUNNING);
        }

        @GetMapping("/illegal-argument-duration")
        public void throwIllegalArgumentForDuration() {
            throw new IllegalArgumentException("Duration must be positive");
        }

        @GetMapping("/illegal-argument-excessive")
        public void throwIllegalArgumentForExcessive() {
            throw new IllegalArgumentException("Duration cannot exceed 24 hours");
        }

        @GetMapping("/illegal-state-start")
        public void throwIllegalStateForStart() {
            throw new IllegalStateException("Cannot start timer in current state");
        }

        @GetMapping("/illegal-state-pause")
        public void throwIllegalStateForPause() {
            throw new IllegalStateException("Cannot pause timer");
        }

        @GetMapping("/runtime-exception")
        public void throwRuntimeException() {
            throw new RuntimeException("Test runtime exception");
        }

        @GetMapping("/generic-exception")
        public void throwGenericException() throws Exception {
            throw new Exception("Test generic exception");
        }
    }
}