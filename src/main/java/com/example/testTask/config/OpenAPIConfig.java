package com.example.testTask.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class OpenAPIConfig {

    @Bean
    public OpenAPI defineOpenAPI () {
        Info info = new Info()
                .title("API для управления товарами")
                .version("1.0");
        return new OpenAPI().info(info);
    }
}
