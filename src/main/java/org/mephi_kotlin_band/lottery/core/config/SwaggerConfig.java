package org.mephi_kotlin_band.lottery.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI otpServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                              .title("OTP Service API")
                              .version("1.0")
                              .description("Документация для сервиса генерации и валидации OTP-кодов"));
    }
}