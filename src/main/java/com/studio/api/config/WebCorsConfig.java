package com.studio.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Allows the deployed studio-web app and local development servers to call
 * the public read APIs and load uploaded files. The allowlist stays explicit
 * so arbitrary origins cannot use the API from a browser.
 */
@Configuration
public class WebCorsConfig implements WebMvcConfigurer {

    private static final String[] WEB_ORIGINS = {
            "https://studio-web-olive.vercel.app",
            "http://localhost:5173",
            "http://localhost:4173"
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(WEB_ORIGINS)
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);

        registry.addMapping("/files/**")
                .allowedOrigins(WEB_ORIGINS)
                .allowedMethods("GET", "HEAD", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
