package com.acupofcoffee.timer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 설정
 * API 문서 자동 생성을 위한 설정
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI timerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Timer API")
                        .description("웹 타이머 애플리케이션 REST API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Timer Development Team")
                                .email("dev@acupofcoffee.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 개발 서버"),
                        new Server()
                                .url("http://localhost:3000/api")
                                .description("프록시를 통한 접근")
                ));
    }
}