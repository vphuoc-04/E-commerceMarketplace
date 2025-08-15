package com.example.common_lib.services;

import org.springframework.security.core.userdetails.UserDetails;

import com.example.common_lib.dtos.UserDto;

public interface UserServiceInterface {
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    boolean validateUserCredentials(String email, String password);
    UserDetails loadUserByUsername(String username);
} 