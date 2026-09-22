package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class RefreshTokenCleanupJob {

    private static final Logger log =
            LoggerFactory.getLogger(RefreshTokenCleanupJob.class);

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenCleanupJob(
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void removeExpiredTokens() {

        log.info("Expired refresh token cleanup started");

        int deleted =
                refreshTokenRepository.deleteExpiredTokens(
                        LocalDateTime.now()
                );

        log.info(
                "Expired refresh token cleanup completed. Deleted tokens: {}",
                deleted
        );
    }
}