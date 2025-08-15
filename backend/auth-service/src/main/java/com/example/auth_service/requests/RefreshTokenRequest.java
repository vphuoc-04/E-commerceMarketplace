package com.example.auth_service.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token cannot be empty")
    private String refreshToken;
}
