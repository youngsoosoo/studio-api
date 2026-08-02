package com.studio.api.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Thread pool used to load the portfolio sections concurrently on a cold
 * aggregate request (see {@code DatabasePortfolioService#getPortfolio}). Sized
 * to run every section at once; threads time out when idle because this only
 * runs on cache misses.
 */
@Configuration
public class AsyncConfig {

    public static final String PORTFOLIO_EXECUTOR = "portfolioTaskExecutor";

    @Bean(PORTFOLIO_EXECUTOR)
    public Executor portfolioTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(12);
        executor.setMaxPoolSize(12);
        executor.setQueueCapacity(32);
        executor.setKeepAliveSeconds(30);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("pf-load-");
        executor.initialize();
        return executor;
    }
}
