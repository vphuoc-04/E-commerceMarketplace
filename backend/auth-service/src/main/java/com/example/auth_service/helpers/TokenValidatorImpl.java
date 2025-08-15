package com.example.auth_service.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.common_lib.helpers.TokenValidator;
import com.example.auth_service.repositories.BlacklistedTokenRepository;
import com.example.auth_service.repositories.RefreshTokenRepository;

@Component
public class TokenValidatorImpl implements TokenValidator {
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    public boolean isBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    @Override
    public boolean isRefreshTokenValid(String token) {
        return refreshTokenRepository.findByRefreshToken(token)
            .map(rt -> rt.getExpiryDate().isAfter(java.time.LocalDateTime.now()))
            .orElse(false);
    }
} 