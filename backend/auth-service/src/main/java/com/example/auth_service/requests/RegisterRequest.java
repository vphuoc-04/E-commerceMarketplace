package com.example.auth_service.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "First name is required")
    private String lastName;

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not in correct format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}   
