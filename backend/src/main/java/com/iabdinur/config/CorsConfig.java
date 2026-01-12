package com.iabdinur.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:5173", "http://blog.localhost:5173")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                    .allowedHeaders("*")
                    .exposedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600); // Cache preflight requests for 1 hour
            }
        };
    }
}

