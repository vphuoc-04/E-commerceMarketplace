package com.example.permission_service.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.common_lib.helpers.TokenValidator;

@Configuration
public class TokenValidatorConfig {
    @Bean
    public TokenValidator tokenValidator() {
        return new TokenValidator() {};
    }
} 