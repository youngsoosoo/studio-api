package com.studio.api.config;

import com.studio.api.common.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Guards /api/admin/** with a single X-Admin-Key header check against the
 * ADMIN_KEY env var. A blank configured key disables the admin API entirely
 * (fail closed). Deliberately not Spring Security: one static key for one
 * admin doesn't warrant a filter chain; revisit if real accounts ever appear.
 */
@Component
public class AdminKeyInterceptor implements HandlerInterceptor {

    public static final String HEADER = "X-Admin-Key";

    private final String adminKey;

    public AdminKeyInterceptor(@Value("${app.admin.key}") String adminKey) {
        this.adminKey = adminKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }
        if (adminKey == null || adminKey.isBlank()) {
            throw new UnauthorizedException("Admin API is disabled");
        }
        String provided = request.getHeader(HEADER);
        if (provided == null || !constantTimeEquals(adminKey, provided)) {
            throw new UnauthorizedException("Invalid admin key");
        }
        return true;
    }

    private boolean constantTimeEquals(String expected, String actual) {
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }
}
