package com.example.auth_service.resources;

import com.example.common_lib.dtos.UserDto;

public class LoginResource {
    private final String token;
    private final String refreshToken;
    private final UserDto user;

    public LoginResource(
        String token,
        String refreshToken,
        UserDto user
    ){
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public UserDto getUser() {
        return user;
    }
}
