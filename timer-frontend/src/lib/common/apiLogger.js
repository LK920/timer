/**
 * Svelte 프론트엔드용 API 로깅 유틸리티
 * 브라우저 콘솔과 서버로 로그를 전송하는 기능 제공
 */

class ApiLogger {
    constructor() {
        this.isDevelopment = import.meta.env.MODE === 'development';
        this.logToServer = true; // 서버 로그 전송 여부
    }

    /**
     * 현재 타임스탬프 생성
     */
    getTimestamp() {
        return new Date().toISOString().replace('T', ' ').substring(0, 23);
    }

    /**
     * 브라우저 정보 수집
     */
    getBrowserInfo() {
        return {
            userAgent: navigator.userAgent,
            url: window.location.href,
            timestamp: this.getTimestamp()
        };
    }

    /**
     * API 요청 로그 기록
     * @param {string} method - HTTP 메소드 (GET, POST, etc.)
     * @param {string} endpoint - API 엔드포인트
     * @param {Object} params - 요청 파라미터
     * @param {Object} headers - 요청 헤더
     */
    logApiRequest(method, endpoint, params = null, headers = null) {
        const timestamp = this.getTimestamp();
        const browserInfo = this.getBrowserInfo();

        const logData = {
            type: 'API_REQUEST',
            timestamp,
            method,
            endpoint,
            params,
            headers,
            browser: browserInfo
        };

        // 콘솔 로그 (개발 환경에서만)
        if (this.isDevelopment) {
            console.log(`🚀 [API REQUEST] [${timestamp}] ${method} ${endpoint}`, {
                params: params || 'none',
                headers: headers || 'default',
                browser: browserInfo.userAgent
            });
        }

        // 서버로 로그 전송 (선택적)
        if (this.logToServer) {
            this.sendLogToServer(logData);
        }
    }

    /**
     * API 응답 로그 기록
     * @param {string} method - HTTP 메소드
     * @param {string} endpoint - API 엔드포인트
     * @param {number} statusCode - 응답 상태 코드
     * @param {Object} response - 응답 데이터
     * @param {number} processingTime - 처리 시간 (밀리초)
     */
    logApiResponse(method, endpoint, statusCode, response = null, processingTime = 0) {
        const timestamp = this.getTimestamp();

        const logData = {
            type: 'API_RESPONSE',
            timestamp,
            method,
            endpoint,
            statusCode,
            response: JSON.stringify(response),
            processingTime,
            browser: this.getBrowserInfo()
        };

        // 콘솔 로그
        const statusIcon = statusCode >= 200 && statusCode < 300 ? '✅' : '❌';
        if (this.isDevelopment) {
            console.log(`${statusIcon} [API RESPONSE] [${timestamp}] ${method} ${endpoint}`, {
                status: statusCode,
                time: `${processingTime}ms`,
                response: response || 'null'
            });
        }

        // 서버로 로그 전송
        if (this.logToServer) {
            this.sendLogToServer(logData);
        }
    }

    /**
     * API 에러 로그 기록
     * @param {string} method - HTTP 메소드
     * @param {string} endpoint - API 엔드포인트
     * @param {Error} error - 에러 객체
     * @param {Object} additionalInfo - 추가 정보
     */
    logApiError(method, endpoint, error, additionalInfo = null) {
        const timestamp = this.getTimestamp();

        const logData = {
            type: 'API_ERROR',
            timestamp,
            method,
            endpoint,
            error: {
                message: error.message,
                stack: error.stack,
                name: error.name
            },
            additionalInfo,
            browser: this.getBrowserInfo()
        };

        // 콘솔 에러 로그
        if (this.isDevelopment) {
            console.error(`💥 [API ERROR] [${timestamp}] ${method} ${endpoint}`, {
                error: error.message,
                additionalInfo,
                stack: error.stack
            });
        }

        // 서버로 로그 전송
        if (this.logToServer) {
            this.sendLogToServer(logData);
        }
    }

    /**
     * 간단한 API 호출 로그
     * @param {string} method - HTTP 메소드
     * @param {string} endpoint - API 엔드포인트
     */
    logSimple(method, endpoint) {
        const timestamp = this.getTimestamp();

        if (this.isDevelopment) {
            console.log(`📡 [API] [${timestamp}] ${method} ${endpoint}`);
        }

        if (this.logToServer) {
            this.sendLogToServer({
                type: 'API_SIMPLE',
                timestamp,
                method,
                endpoint,
                browser: this.getBrowserInfo()
            });
        }
    }

    /**
     * 서버로 로그 전송
     * @param {Object} logData - 로그 데이터
     */
    async sendLogToServer(logData) {
        try {
            // 실제 환경에서는 로그 수집 서버 엔드포인트로 전송
            // 여기서는 콘솔에 표시만 함
            if (this.isDevelopment) {
                console.log('📤 [LOG TO SERVER]', logData);
            }

            // 실제 서버 전송 코드 예시:
            // await fetch('/api/logs', {
            //     method: 'POST',
            //     headers: { 'Content-Type': 'application/json' },
            //     body: JSON.stringify(logData)
            // });
        } catch (error) {
            console.error('Failed to send log to server:', error);
        }
    }

    /**
     * 서버 로그 전송 설정
     * @param {boolean} enabled - 서버 로그 전송 여부
     */
    setServerLogging(enabled) {
        this.logToServer = enabled;
    }
}

// 싱글톤 인스턴스 생성 및 export
export const apiLogger = new ApiLogger();

// 편의 함수들 export
export const logApiRequest = (method, endpoint, params, headers) =>
    apiLogger.logApiRequest(method, endpoint, params, headers);

export const logApiResponse = (method, endpoint, statusCode, response, processingTime) =>
    apiLogger.logApiResponse(method, endpoint, statusCode, response, processingTime);

export const logApiError = (method, endpoint, error, additionalInfo) =>
    apiLogger.logApiError(method, endpoint, error, additionalInfo);

export const logSimple = (method, endpoint) =>
    apiLogger.logSimple(method, endpoint);

export default apiLogger;