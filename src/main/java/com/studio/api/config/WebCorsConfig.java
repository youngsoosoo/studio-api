package com.studio.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Allows the local studio-web dev server to call the API during integration.
 * Kept intentionally narrow (single localhost origin, read-only methods);
 * widen via configuration when deploying.
 */
@Configuration
public class WebCorsConfig implements WebMvcConfigurer {

    private static final String[] LOCAL_WEB_ORIGINS = {
            "http://localhost:5173",
            "http://localhost:4173"
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(LOCAL_WEB_ORIGINS)
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);

        registry.addMapping("/files/**")
                .allowedOrigins(LOCAL_WEB_ORIGINS)
                .allowedMethods("GET", "HEAD", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
