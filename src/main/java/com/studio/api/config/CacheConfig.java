package com.studio.api.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * In-memory caching for the read APIs. Portfolio content changes infrequently,
 * so the aggregate and project details are cached and served from memory after
 * the first request. {@code ImageService} evicts these caches on upload so a
 * newly uploaded photo shows immediately.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String PORTFOLIO_CACHE = "portfolio";
    public static final String PROJECT_DETAILS_CACHE = "projectDetails";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(PORTFOLIO_CACHE, PROJECT_DETAILS_CACHE);
    }
}
