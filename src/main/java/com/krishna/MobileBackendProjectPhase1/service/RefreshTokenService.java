package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.entity.RefreshToken;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.exception.InvalidRefreshTokenException;
import com.krishna.MobileBackendProjectPhase1.repository.RefreshTokenRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // Refresh token valid for 30 days
    private static final long REFRESH_TOKEN_DAYS = 30;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {

        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {

        // Remove previous refresh token of this user
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken.setUser(user);

        refreshToken.setExpiryDate(
                LocalDateTime.now()
                        .plusDays(REFRESH_TOKEN_DAYS)
        );

        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {

        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException(
                                "Invalid refresh token"
                        )
                );
    }

    @Transactional
    public RefreshToken verifyToken(String token) {

        RefreshToken refreshToken = findByToken(token);

        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException(
                    "Refresh token has been revoked"
            );
        }

        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new InvalidRefreshTokenException(
                    "Refresh token has expired"
            );
        }

        return refreshToken;
    }

    @Transactional
    public void revokeToken(String token) {

        RefreshToken refreshToken = findByToken(token);

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void revokeUserTokens(User user) {

        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshTokenRepository.save(refreshToken);
                });
    }
}