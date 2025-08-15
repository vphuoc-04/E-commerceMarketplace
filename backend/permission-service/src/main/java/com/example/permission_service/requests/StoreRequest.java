package com.example.permission_service.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreRequest {
    @NotBlank
    private String name;
    @NotNull
    private Integer publish;
    private String description;
} 