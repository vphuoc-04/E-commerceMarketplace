package com.example.auth_service.controllers;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth_service.entities.RefreshToken;
import com.example.auth_service.repositories.RefreshTokenRepository;
import com.example.auth_service.requests.BlacklistedTokenRequest;
import com.example.auth_service.requests.LoginRequest;
import com.example.auth_service.requests.RefreshTokenRequest;
import com.example.auth_service.requests.RegisterRequest;
import com.example.auth_service.resources.RefreshTokenResource;
import com.example.auth_service.services.AuthService;
import com.example.auth_service.services.impl.BlacklistedTokenService;
import com.example.auth_service.services.interfaces.UserServiceInterface;
import com.example.common_lib.configs.JwtConfig;
import com.example.common_lib.dtos.AuthResponse;
import com.example.common_lib.dtos.UserDto;
import com.example.common_lib.resources.ApiResource;
import com.example.common_lib.services.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserServiceInterface userService;

    @Autowired
    private BlacklistedTokenService blacklistedTokenService;

    @Autowired 
    private JwtService jwtService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public AuthController(
        UserServiceInterface userService
    ){
        this.userService = userService;
    }

    @PostMapping("register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Register endpoint should be handled by user service");
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            boolean isValid = userService.validateUserCredentials(request.getEmail(), request.getPassword());
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResource.message("Invalid credentials", HttpStatus.UNAUTHORIZED));
            }
            UserDto user = userService.getUserByEmail(request.getEmail());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResource.message("User not found", HttpStatus.UNAUTHORIZED));
            }
            // Generate tokens
            String accessToken = jwtService.generateToken(user.getId(), user.getEmail(), null);
            String refreshToken = authService.generateRefreshToken(user.getId(), user.getEmail());
            // Create response
            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(accessToken);
            authResponse.setRefreshToken(refreshToken);
            authResponse.setExpiresIn(jwtConfig.getExpirationTime());
            authResponse.setUser(user);
            ResponseCookie cookie = ResponseCookie.from("cookies", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMillis(jwtConfig.getExpirationTime()))
                .build();
            return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(ApiResource.ok(authResponse, "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.message("Login failed", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("blacklisted_token")
    public ResponseEntity<?> addTokenToBlacklist(@Valid @RequestBody BlacklistedTokenRequest request) {
        try {
            Object result = blacklistedTokenService.create(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.message("NETWORK_ERROR", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("refresh_token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest httpRequest) {
        String refreshToken = request.getRefreshToken();

        if (!authService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResource.message("Refresh token is invalid", HttpStatus.UNAUTHORIZED));
        }

        Optional<RefreshToken> dbRefreshTokenOptional = refreshTokenRepository.findByRefreshToken(refreshToken);

        if(dbRefreshTokenOptional.isPresent()) {
            RefreshToken dbRefreshToken = dbRefreshTokenOptional.get();
            Long userId = dbRefreshToken.getUserId();
            String accessToken = httpRequest.getHeader("Authorization");
            if (accessToken == null || accessToken.isEmpty()) {
                // Nếu không có token, sinh mới cho userId
                // Lưu ý: chỉ nên làm nếu bạn chắc chắn userId hợp lệ và bảo mật
                accessToken = "Bearer " + jwtService.generateToken(userId, "", null);
            }
            UserDto user = ((com.example.auth_service.services.impl.UserService) userService).getUserById(userId, accessToken);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResource.message("User not found", HttpStatus.UNAUTHORIZED));
            }
            String email = user.getEmail();

            String newToken = jwtService.generateToken(userId, email, null);
            String newRefreshToken = authService.generateRefreshToken(userId, email);

            ResponseCookie accessTokenCookie = ResponseCookie.from("cookies", newToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofMillis(jwtConfig.getExpirationTime())) 
                .build();

            ApiResource<RefreshTokenResource> response = ApiResource.ok(new RefreshTokenResource(newToken, newRefreshToken), "SUCCESS");

            return ResponseEntity.ok().header("Set-Cookie", accessTokenCookie.toString()).body(response);
        }
        return ResponseEntity.internalServerError().body(ApiResource.message("NETWORK_ERROR", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("logout") 
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);

            // Get user ID from token
            String userIdStr = jwtService.getUserIdFromJwt(token);
            Long userId = Long.parseLong(userIdStr);

            // Blacklist the token
            authService.blacklistToken(token, userId, "LOGOUT");

            ResponseCookie clearCookie = ResponseCookie.from("cookies", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

            return ResponseEntity.ok()
                .header("Set-Cookie", clearCookie.toString())
                .body(ApiResource.message("Logout successful", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.message("NETWORK_ERROR", HttpStatus.UNAUTHORIZED));
        }
    }
}
