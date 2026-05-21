package com.ferry.framework.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI ferryOpenApi() {
        return new OpenAPI().info(new Info().title("Ferry-Mall API").version("1.0.0"));
    }
}
