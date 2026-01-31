package com.devcommunity.service;

import com.devcommunity.entity.RefreshToken;
import com.devcommunity.entity.User;
import com.devcommunity.exception.DeveloperCommunityException;
import com.devcommunity.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    
    @Value("${jwt.refresh-token-expiration:604800000}") // 7 days in milliseconds
    private long refreshTokenDurationMs;
    
    private final RefreshTokenRepository refreshTokenRepository;
    
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
    
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
            .user(user)
            .token(UUID.randomUUID().toString())
            .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
            .createdAt(Instant.now())
            .build();
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    public RefreshToken verifyExpiration(RefreshToken token) throws DeveloperCommunityException {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new DeveloperCommunityException("Refresh token expired. Please sign in again.");
        }
        
        if (token.isRevoked()) {
            throw new DeveloperCommunityException("Refresh token has been revoked.");
        }
        
        return token;
    }
    
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
    
    public RefreshToken findByToken(String token) throws DeveloperCommunityException {
        return refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> new DeveloperCommunityException("Refresh token not found"));
    }
    
    @Transactional
    public void revokeToken(String token) throws DeveloperCommunityException {
        RefreshToken refreshToken = findByToken(token);
        refreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
    }
    
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(Instant.now());
    }
}
