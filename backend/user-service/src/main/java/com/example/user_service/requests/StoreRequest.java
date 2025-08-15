package com.example.user_service.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StoreRequest {
    @NotBlank(message = "Member group name cannot be empty")
    private String name;

    @NotNull(message = "Status cannot be empty")
    @Min(value = 0, message = "Status value must be greater than or equal to 0")
    @Max(value = 2, message = "Status value must be less than or equal to 2")
    private Integer publish;
}
