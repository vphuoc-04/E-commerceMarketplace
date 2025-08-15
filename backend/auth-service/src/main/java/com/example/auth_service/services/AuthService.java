package com.example.auth_service.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.auth_service.entities.BlacklistedToken;
import com.example.auth_service.entities.RefreshToken;
import com.example.auth_service.repositories.BlacklistedTokenRepository;
import com.example.auth_service.repositories.RefreshTokenRepository;
import com.example.common_lib.configs.JwtConfig;
import com.example.common_lib.services.JwtService;

@Service
public class AuthService extends JwtService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(JwtConfig jwtConfig, BlacklistedTokenRepository blacklistedTokenRepository, RefreshTokenRepository refreshTokenRepository) {
        super(jwtConfig);
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    protected JwtConfig getJwtConfig() {
        return this.jwtConfig;
    }

    public boolean isBlacklistedToken(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    public void blacklistToken(String token, Long userId, String type) {
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setUserId(userId);
        blacklistedToken.setType(type);
        blacklistedToken.setExpiryDate(java.time.LocalDateTime.now().plusDays(1));
        blacklistedTokenRepository.save(blacklistedToken);
    }

    public String generateRefreshToken(Long userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationRefreshToken());
        LocalDateTime localExpiryDate = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String refreshToken = UUID.randomUUID().toString();
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserId(userId);
        if (optionalRefreshToken.isPresent()) {
            RefreshToken dbRefreshToken = optionalRefreshToken.get();
            dbRefreshToken.setRefreshToken(refreshToken);
            dbRefreshToken.setExpiryDate(localExpiryDate);
            refreshTokenRepository.save(dbRefreshToken);
        } else {
            RefreshToken insertRefreshToken = new RefreshToken();
            insertRefreshToken.setRefreshToken(refreshToken);
            insertRefreshToken.setExpiryDate(localExpiryDate);
            insertRefreshToken.setUserId(userId);
            refreshTokenRepository.save(insertRefreshToken);
        }
        return refreshToken;
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token does not exist"));
            return refreshToken.getExpiryDate().isAfter(LocalDateTime.now());
        } catch (Exception e) {
            return false;
        }
    }
} 