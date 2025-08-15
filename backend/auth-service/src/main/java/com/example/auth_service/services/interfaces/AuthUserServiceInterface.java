package com.example.auth_service.services.interfaces;

import com.example.common_lib.dtos.UserDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthUserServiceInterface {
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    boolean validateUserCredentials(String email, String password);
    UserDetails loadUserByUsername(String username);
} 