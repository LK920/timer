package com.acupofcoffee.timer.config;

import com.acupofcoffee.timer.common.ApiLoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ApiLoggingFilter> apiLoggingFilter() {
        FilterRegistrationBean<ApiLoggingFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ApiLoggingFilter());
        registrationBean.addUrlPatterns("/api/*"); // API 경로에만 필터 적용
        registrationBean.setOrder(1); // 필터 실행 순서
        registrationBean.setName("ApiLoggingFilter");

        return registrationBean;
    }
}