package com.devcommunity.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.devcommunity.service.RefreshTokenService;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);
    private final RefreshTokenService refreshTokenService;
    
    public SchedulingConfig(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }
    
    /**
     * Cleanup expired refresh tokens daily at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupExpiredTokens() {
        logger.info("Running scheduled cleanup of expired refresh tokens");
        refreshTokenService.cleanupExpiredTokens();
        logger.info("Expired refresh tokens cleanup completed");
    }
}
