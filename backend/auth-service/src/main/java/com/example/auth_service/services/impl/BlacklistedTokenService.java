package com.example.auth_service.services.impl;

import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.auth_service.entities.BlacklistedToken;
import com.example.auth_service.repositories.BlacklistedTokenRepository;
import com.example.auth_service.requests.BlacklistedTokenRequest;
import com.example.common_lib.resources.ApiResource;
import com.example.common_lib.services.JwtService;

import io.jsonwebtoken.Claims;

@Service
public class BlacklistedTokenService {
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private JwtService jwtService;

    public Object create(BlacklistedTokenRequest request) {
        if (blacklistedTokenRepository.existsByToken(request.getToken())) {
            return ApiResource.error("TOKEN_ERROR", "Token exists in blacklist!", HttpStatus.BAD_REQUEST);
        }

        Claims claims = jwtService.getAllClaimsFromToken(request.getToken());

        Long userId = Long.valueOf(claims.getSubject());

        Date expiryDate = claims.getExpiration();
        BlacklistedToken blacklistToken = new BlacklistedToken();
        blacklistToken.setToken(request.getToken());
        blacklistToken.setUserId(userId);
        blacklistToken.setExpiryDate(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        blacklistedTokenRepository.save(blacklistToken);

        return ApiResource.message("Token successfully added to token blacklist", HttpStatus.OK);
    }
}
