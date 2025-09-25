<script>
    import { onMount, onDestroy } from 'svelte';
    import { logApiRequest, logApiResponse, logApiError } from '$lib/common/apiLogger.js';

    // API 설정 - 개발 환경과 Docker 환경에서 다르게 설정
    const API_BASE_URL = import.meta.env.MODE === 'development'
        ? 'http://localhost:8080/api/timer'
        : '/api/timer';

    // 상태 변수들
    let displayTime = '00:00:00';
    let inputMinutes = '';
    let isRunning = false;
    let timerInterval;
    let lastKnownRemainingSeconds = 0;

    // 시간 포맷팅 함수
    function formatTime(totalSeconds) {
        const h = String(Math.floor(totalSeconds / 3600)).padStart(2, '0');
        const m = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, '0');
        const s = String(totalSeconds % 60).padStart(2, '0');
        return `${h}:${m}:${s}`;
    }

    // 서버에서 타이머 상태 가져오기
    async function fetchTimerState() {
        const startTime = Date.now();

        try {
            // API 요청 로그
            logApiRequest('GET', API_BASE_URL);

            const response = await fetch(API_BASE_URL);
            const data = await response.json();

            // API 응답 로그
            const processingTime = Date.now() - startTime;
            logApiResponse('GET', API_BASE_URL, response.status, data, processingTime);

            isRunning = data.running;
            lastKnownRemainingSeconds = data.currentRemainingSeconds;
            displayTime = formatTime(lastKnownRemainingSeconds);

            if (isRunning) {
                startFrontendTimer();
            } else {
                clearInterval(timerInterval);
            }
        } catch (error) {
            // API 에러 로그
            logApiError('GET', API_BASE_URL, error);
            console.error('Failed to fetch timer state:', error);
            clearInterval(timerInterval);
        }
    }

    // 프론트엔드에서 타이머 업데이트
    function startFrontendTimer() {
        clearInterval(timerInterval);
        timerInterval = setInterval(() => {
            if (isRunning && lastKnownRemainingSeconds > 0) {
                lastKnownRemainingSeconds--;
                displayTime = formatTime(lastKnownRemainingSeconds);
            } else {
                clearInterval(timerInterval);
                isRunning = false;
                if (lastKnownRemainingSeconds <= 0) {
                    alert('타이머 종료!');
                    fetchTimerState();
                }
            }
        }, 1000);
    }

    // 시작 버튼 클릭 핸들러
    async function handleStart() {
        let duration = parseInt(inputMinutes);
        if (isNaN(duration) || duration <= 0) {
            alert('유효한 분을 입력해주세요.');
            return;
        }

        const startTime = Date.now();
        const endpoint = `${API_BASE_URL}/start?durationSeconds=${duration * 60}`;

        try {
            // API 요청 로그
            logApiRequest('POST', endpoint, { durationSeconds: duration * 60 });

            const response = await fetch(endpoint, {
                method: 'POST'
            });

            // API 응답 로그
            const processingTime = Date.now() - startTime;
            logApiResponse('POST', endpoint, response.status, null, processingTime);

            if (response.ok) {
                inputMinutes = '';
                await fetchTimerState();
            } else {
                console.error('Failed to start timer on backend.');
            }
        } catch (error) {
            // API 에러 로그
            logApiError('POST', endpoint, error);
            console.error('Error starting timer:', error);
        }
    }

    // 일시정지 버튼 클릭 핸들러
    async function handlePause() {
        const startTime = Date.now();
        const endpoint = `${API_BASE_URL}/pause`;

        try {
            logApiRequest('POST', endpoint);

            const response = await fetch(endpoint, {
                method: 'POST'
            });

            const processingTime = Date.now() - startTime;
            logApiResponse('POST', endpoint, response.status, null, processingTime);

            if (response.ok) {
                await fetchTimerState();
            } else {
                console.error('Failed to pause timer on backend.');
            }
        } catch (error) {
            logApiError('POST', endpoint, error);
            console.error('Error pausing timer:', error);
        }
    }

    // 재개 버튼 클릭 핸들러
    async function handleResume() {
        const startTime = Date.now();
        const endpoint = `${API_BASE_URL}/resume`;

        try {
            logApiRequest('POST', endpoint);

            const response = await fetch(endpoint, {
                method: 'POST'
            });

            const processingTime = Date.now() - startTime;
            logApiResponse('POST', endpoint, response.status, null, processingTime);

            if (response.ok) {
                await fetchTimerState();
            } else {
                console.error('Failed to resume timer on backend.');
            }
        } catch (error) {
            logApiError('POST', endpoint, error);
            console.error('Error resuming timer:', error);
        }
    }

    // 초기화 버튼 클릭 핸들러
    async function handleReset() {
        const startTime = Date.now();
        const endpoint = `${API_BASE_URL}/reset`;

        try {
            logApiRequest('POST', endpoint);

            const response = await fetch(endpoint, {
                method: 'POST'
            });

            const processingTime = Date.now() - startTime;
            logApiResponse('POST', endpoint, response.status, null, processingTime);

            if (response.ok) {
                await fetchTimerState();
            } else {
                console.error('Failed to reset timer on backend.');
            }
        } catch (error) {
            logApiError('POST', endpoint, error);
            console.error('Error resetting timer:', error);
        }
    }

    // 컴포넌트 마운트 시 초기 상태 불러오기
    onMount(() => {
        fetchTimerState();
    });

    // 컴포넌트 파괴 시 인터벌 정리
    onDestroy(() => {
        clearInterval(timerInterval);
    });
</script>

<div class="mobile-app-container">
    <div class="header">
        <h1>간단한 타이머</h1>
    </div>

    <div class="timer-display-wrapper">
        <div class="timer-display">{displayTime}</div>
    </div>

    <div class="controls">
        <div class="input-group">
            <input
                type="number"
                bind:value={inputMinutes}
                placeholder="분 설정 (예: 5)"
                min="1"
                disabled={isRunning}
            />
        </div>

        <div class="button-group">
            {#if !isRunning && lastKnownRemainingSeconds <= 0}
                <button on:click={handleStart} class="btn-primary">시작</button>
            {:else if isRunning}
                <button on:click={handlePause} class="btn-warning">일시정지</button>
            {:else}
                <button on:click={handleResume} class="btn-primary">재개</button>
            {/if}
            <button on:click={handleReset} class="btn-danger">초기화</button>
        </div>
    </div>
</div>