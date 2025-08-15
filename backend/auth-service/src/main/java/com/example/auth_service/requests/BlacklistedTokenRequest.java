package com.example.auth_service.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlacklistedTokenRequest {
    @NotBlank(message = "Token can not be empty!")
    private String token;
}
