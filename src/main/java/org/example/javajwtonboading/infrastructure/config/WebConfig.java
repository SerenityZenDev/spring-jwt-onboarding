package org.example.javajwtonboading.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/v3/api-docs").allowedOrigins("http://localhost:8080");
        registry.addMapping("/swagger-ui/**").allowedOrigins("http://localhost:8080");
    }
}
