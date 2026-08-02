package com.studio.api.config;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serves uploaded files at /files/** and applies the admin-key check to
 * /api/admin/**. The upload dir is resolved through Path.toUri() so Windows
 * paths (including non-ASCII segments) work as resource locations.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AdminKeyInterceptor adminKeyInterceptor;
    private final String uploadDir;

    public WebConfig(AdminKeyInterceptor adminKeyInterceptor, @Value("${app.upload.dir}") String uploadDir) {
        this.adminKeyInterceptor = adminKeyInterceptor;
        this.uploadDir = uploadDir;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminKeyInterceptor).addPathPatterns("/api/admin/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Path.of(uploadDir).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/files/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }
}
