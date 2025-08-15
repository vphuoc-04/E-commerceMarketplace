package com.example.common_lib.helpers;

public interface TokenValidator {
    default boolean isBlacklisted(String token) { return false; }
    default boolean isRefreshTokenValid(String token) { return true; }
} 