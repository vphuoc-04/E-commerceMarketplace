package com.example.auth_service.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.auth_service.helpers.TokenValidatorImpl;
import com.example.auth_service.services.impl.UserService;
import com.example.common_lib.helpers.JwtAuthFilter;
import com.example.common_lib.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final TokenValidatorImpl tokenValidator;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return (UserDetailsService) userService;
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService, ObjectMapper objectMapper, TokenValidatorImpl tokenValidator) {
        return new JwtAuthFilter(jwtService, userDetailsService, objectMapper, tokenValidator);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService, UserDetailsService userDetailsService, ObjectMapper objectMapper, TokenValidatorImpl tokenValidator) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/auth/login",
                    "/api/v1/auth/refresh_token",
                    "/api/v1/auth/register"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(new JwtAuthFilter(jwtService, userDetailsService, objectMapper, tokenValidator), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
} 