package com.acupofcoffee.timer.common;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ApiLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ApiLoggingFilter.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // API 경로만 로깅 (정적 리소스 제외)
        String requestURI = httpRequest.getRequestURI();
        if (!isApiRequest(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        // 요청/응답 내용을 캐싱할 수 있는 래퍼로 감싸기
        ContentCachingRequestWrapper cachedRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper cachedResponse = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();

        // 요청 로그 기록
        logRequest(cachedRequest);

        try {
            // 다음 필터 또는 서블릿 실행
            chain.doFilter(cachedRequest, cachedResponse);
        } finally {
            // 응답 로그 기록
            long processingTime = System.currentTimeMillis() - startTime;
            logResponse(cachedRequest, cachedResponse, processingTime);

            // 응답 내용을 실제 응답으로 복사 (중요!)
            cachedResponse.copyBodyToResponse();
        }
    }

    /**
     * API 요청인지 확인
     */
    private boolean isApiRequest(String requestURI) {
        return requestURI.startsWith("/api/");
    }

    /**
     * 요청 로그 기록
     */
    private void logRequest(ContentCachingRequestWrapper request) {
        String timestamp = LocalDateTime.now().format(formatter);
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientInfo = getClientInfo(request);

        // 요청 파라미터 수집
        Map<String, String> parameters = getRequestParameters(request);

        // 요청 본문 (POST/PUT 등의 경우)
        String requestBody = getRequestBody(request);

        String logMessage = String.format(
            "[API REQUEST] [%s] %s %s%s | Client: %s | Params: %s%s",
            timestamp, method, requestURI,
            queryString != null ? "?" + queryString : "",
            clientInfo,
            parameters.isEmpty() ? "none" : parameters,
            !requestBody.isEmpty() ? " | Body: " + requestBody : ""
        );

        logger.info(logMessage);
    }

    /**
     * 응답 로그 기록
     */
    private void logResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, long processingTime) {
        String timestamp = LocalDateTime.now().format(formatter);
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        int statusCode = response.getStatus();
        String responseBody = getResponseBody(response);

        String logMessage = String.format(
            "[API RESPONSE] [%s] %s %s | Status: %d | Time: %dms | Response: %s",
            timestamp, method, requestURI, statusCode, processingTime,
            responseBody.isEmpty() ? "empty" : responseBody
        );

        if (statusCode >= 400) {
            logger.error(logMessage);
        } else {
            logger.info(logMessage);
        }
    }

    /**
     * 클라이언트 정보 수집
     */
    private String getClientInfo(HttpServletRequest request) {
        String ip = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        return String.format("IP: %s, UserAgent: %s", ip, userAgent != null ? userAgent : "unknown");
    }

    /**
     * 실제 클라이언트 IP 주소 추출 (프록시 고려)
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * 요청 파라미터 수집
     */
    private Map<String, String> getRequestParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName, paramValue);
        }

        return parameters;
    }

    /**
     * 요청 본문 추출
     */
    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content);
            // 응답 길이 제한 (너무 긴 경우 잘라내기)
            return body.length() > 500 ? body.substring(0, 500) + "..." : body;
        }
        return "";
    }

    /**
     * 응답 본문 추출
     */
    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content);
            // 응답 길이 제한 (너무 긴 경우 잘라내기)
            return body.length() > 500 ? body.substring(0, 500) + "..." : body;
        }
        return "";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("ApiLoggingFilter initialized");
    }

    @Override
    public void destroy() {
        logger.info("ApiLoggingFilter destroyed");
    }
}