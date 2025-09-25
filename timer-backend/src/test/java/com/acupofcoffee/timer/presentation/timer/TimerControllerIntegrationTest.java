package com.acupofcoffee.timer.presentation.timer;

import com.acupofcoffee.timer.domain.timer.TimerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureTestMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TimerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TimerRepository timerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        timerRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/timer - 초기 상태 조회")
    void shouldGetInitialTimerState() throws Exception {
        mockMvc.perform(get("/api/timer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.running", is(false)))
                .andExpect(jsonPath("$.startTime", is(0)))
                .andExpect(jsonPath("$.durationSeconds", is(0)))
                .andExpect(jsonPath("$.remainingSeconds", is(0)))
                .andExpected(jsonPath("$.currentRemainingSeconds", is(0)));
    }

    @Test
    @DisplayName("POST /api/timer/start - 타이머 시작 성공")
    void shouldStartTimerSuccessfully() throws Exception {
        mockMvc.perform(post("/api/timer/start")
                        .param("durationSeconds", "300")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpected(content().contentType(MediaType.APPLICATION_JSON))
                .andExpected(jsonPath("$.running", is(true)))
                .andExpected(jsonPath("$.startTime", greaterThan(0)))
                .andExpected(jsonPath("$.durationSeconds", is(300)))
                .andExpected(jsonPath("$.remainingSeconds", is(300)))
                .andExpected(jsonPath("$.currentRemainingSeconds", lessThanOrEqualTo(300)));
    }

    @Test
    @DisplayName("POST /api/timer/start - 잘못된 파라미터")
    void shouldReturnBadRequestForInvalidDuration() throws Exception {
        mockMvc.perform(post("/api/timer/start")
                        .param("durationSeconds", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpected(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/timer/start - 24시간 초과")
    void shouldReturnBadRequestForExcessiveDuration() throws Exception {
        long excessiveDuration = 25 * 3600; // 25시간

        mockMvc.perform(post("/api/timer/start")
                        .param("durationSeconds", String.valueOf(excessiveDuration))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpected(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/timer/pause - 일시정지 성공")
    void shouldPauseTimerSuccessfully() throws Exception {
        // 먼저 타이머 시작
        mockMvc.perform(post("/api/timer/start")
                        .param("durationSeconds", "300"))
                .andExpected(status().isOk());

        // 일시정지
        mockMvc.perform(post("/api/timer/pause")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.running", is(false)))
                .andExpected(jsonPath("$.durationSeconds", is(300)));
    }

    @Test
    @DisplayName("POST /api/timer/pause - 일시정지 불가 상태")
    void shouldReturnConflictWhenCannotPause() throws Exception {
        mockMvc.perform(post("/api/timer/pause")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpected(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/timer/resume - 재개 성공")
    void shouldResumeTimerSuccessfully() throws Exception {
        // 타이머 시작 후 일시정지
        mockMvc.perform(post("/api/timer/start")
                        .param("durationSeconds", "300"))
                .andExpected(status().isOk());

        mockMvc.perform(post("/api/timer/pause"))
                .andExpected(status().isOk());

        // 재개
        mockMvc.perform(post("/api/timer/resume")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.running", is(true)));
    }

    @Test
    @DisplayName("POST /api/timer/resume - 재개 불가 상태")
    void shouldReturnConflictWhenCannotResume() throws Exception {
        mockMvc.perform(post("/api/timer/resume")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpected(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/timer/reset - 초기화 성공")
    void shouldResetTimerSuccessfully() throws Exception {
        // 타이머 시작 후 초기화
        mockMvc.perform(post("/api/timer/start")
                        .param("durationSeconds", "300"))
                .andExpected(status().isOk());

        mockMvc.perform(post("/api/timer/reset")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.running", is(false)))
                .andExpected(jsonPath("$.durationSeconds", is(0)))
                .andExpected(jsonPath("$.remainingSeconds", is(0)))
                .andExpected(jsonPath("$.currentRemainingSeconds", is(0)));
    }

    @Test
    @DisplayName("전체 워크플로우 통합 테스트")
    void shouldCompleteFullWorkflowThroughAPI() throws Exception {
        // 1. 초기 상태 확인
        mockMvc.perform(get("/api/timer"))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.running", is(false)));

        // 2. 타이머 시작
        mockMvc.perform(post("/api/timer/start")
                        .param("durationSeconds", "2"))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.running", is(true)))
                .andExpected(jsonPath("$.durationSeconds", is(2)));

        // 3. 일시정지
        mockMvc.perform(post("/api/timer/pause"))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.running", is(false)));

        // 4. 재개
        mockMvc.perform(post("/api/timer/resume"))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.running", is(true)));

        // 5. 초기화
        mockMvc.perform(post("/api/timer/reset"))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.running", is(false)))
                .andExpected(jsonPath("$.durationSeconds", is(0)));
    }

    @Test
    @DisplayName("이미 실행 중인 타이머 시작 시도")
    void shouldReturnConflictWhenStartingAlreadyRunningTimer() throws Exception {
        // 첫 번째 타이머 시작
        mockMvc.perform(post("/api/timer/start")
                        .param("durationSeconds", "300"))
                .andExpected(status().isOk());

        // 두 번째 타이머 시작 시도
        mockMvc.perform(post("/api/timer/start")
                        .param("durationSeconds", "600"))
                .andExpected(status().isConflict());
    }

    @Test
    @DisplayName("CORS 헤더 확인")
    void shouldIncludeCorsHeaders() throws Exception {
        mockMvc.perform(get("/api/timer")
                        .header("Origin", "http://localhost:3000"))
                .andExpected(status().isOk())
                .andExpected(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    @DisplayName("타이머 상태 변화 추적")
    void shouldTrackTimerStateChanges() throws Exception {
        // 타이머 시작
        mockMvc.perform(post("/api/timer/start")
                        .param("durationSeconds", "5"))
                .andExpected(status().isOk());

        // 잠시 대기 후 상태 확인
        Thread.sleep(100);

        mockMvc.perform(get("/api/timer"))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.running", is(true)))
                .andExpected(jsonPath("$.currentRemainingSeconds", lessThan(5)));
    }

    @Test
    @DisplayName("Content-Type 헤더 검증")
    void shouldReturnCorrectContentType() throws Exception {
        mockMvc.perform(get("/api/timer"))
                .andExpected(status().isOk())
                .andExpected(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/timer/start")
                        .param("durationSeconds", "300"))
                .andExpected(status().isOk())
                .andExpected(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("잘못된 HTTP 메소드 요청")
    void shouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(put("/api/timer"))
                .andExpected(status().isMethodNotAllowed());

        mockMvc.perform(delete("/api/timer"))
                .andExpected(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("존재하지 않는 엔드포인트 요청")
    void shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/timer/nonexistent"))
                .andExpected(status().isNotFound());
    }

    @Test
    @DisplayName("JSON 응답 구조 검증")
    void shouldReturnCorrectJsonStructure() throws Exception {
        mockMvc.perform(get("/api/timer"))
                .andExpected(status().isOk())
                .andExpected(jsonPath("$.running").exists())
                .andExpected(jsonPath("$.startTime").exists())
                .andExpected(jsonPath("$.durationSeconds").exists())
                .andExpected(jsonPath("$.remainingSeconds").exists())
                .andExpected(jsonPath("$.currentRemainingSeconds").exists())
                .andExpected(jsonPath("$.running").isBoolean())
                .andExpected(jsonPath("$.startTime").isNumber())
                .andExpected(jsonPath("$.durationSeconds").isNumber())
                .andExpected(jsonPath("$.remainingSeconds").isNumber())
                .andExpected(jsonPath("$.currentRemainingSeconds").isNumber());
    }
}