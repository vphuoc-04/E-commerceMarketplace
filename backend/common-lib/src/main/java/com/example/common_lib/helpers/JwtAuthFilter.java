package com.example.common_lib.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.common_lib.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final TokenValidator tokenValidator;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService, ObjectMapper objectMapper, TokenValidator tokenValidator) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        this.tokenValidator = tokenValidator;
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean skip = path.startsWith("/api/v1/auth/login") ||
            path.startsWith("/api/v1/auth/refresh_token") ||
            path.startsWith("/api/v1/auth/register") ||
            path.startsWith("/api/v1/users/validate") ||
            path.startsWith("/api/v1/users/email/") ||
            (path.matches("/api/v1/users/\\d+$") && request.getMethod().equals("GET")) ||
            path.startsWith("/api/v1/user_catalogue_permission/") ||
            path.startsWith("/api/v1/user_catalogue_user/");
        System.out.println("[JwtAuthFilter] shouldNotFilter path: " + path + " => " + skip);
        return skip;
    }

    @Override
    public void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userId;
    
            if (authHeader == null || !authHeader.startsWith("Bearer ")){
                sendErrorResponse(
                    response, 
                    request, 
                    HttpServletResponse.SC_UNAUTHORIZED, 
                    "Authentication failed!", 
                    "Token not found!"
                );
                return;
            }

            jwt = authHeader.substring(7);

            if(!jwtService.isTokenFormatValid(jwt)){
                sendErrorResponse(response, 
                    request, 
                    HttpServletResponse.SC_UNAUTHORIZED, 
                    "Authentication failed!", 
                    "Token is not in correct format!"
                );
                return;
            }
    
            if(jwtService.isTokenExpired(jwt)){
                sendErrorResponse(response, 
                    request, 
                    HttpServletResponse.SC_UNAUTHORIZED, 
                    "Authentication failed!", 
                    "Token has expired!"
                );
                return;
            }
    
            if(!jwtService.isSignatureToken(jwt)){
                sendErrorResponse(response, 
                    request, 
                    HttpServletResponse.SC_UNAUTHORIZED, 
                    "Authentication failed!", 
                    "Invalid signature token!"
                );
                return;
            }
    
            if(!jwtService.isIssuerToken(jwt)){
                sendErrorResponse(response, 
                    request, 
                    HttpServletResponse.SC_UNAUTHORIZED, 
                    "Authentication failed!", 
                    "Invalid token origin!"
                );
                return;
            }

            if (tokenValidator.isBlacklisted(jwt)) {
                sendErrorResponse(response, 
                    request, 
                    HttpServletResponse.SC_UNAUTHORIZED, 
                    "Authentication failed!", 
                    "Token has been blacklisted!"
                );
                return;
            }
    
            userId = jwtService.getUserIdFromJwt(jwt);
    
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                final String emailFromToken = jwtService.getEmailFromJwt(jwt);
                if (!emailFromToken.equals(userDetails.getUsername())){
                    // Nếu userDetails là dummy (password rỗng), bỏ qua so sánh
                    if (userDetails.getPassword() == null || userDetails.getPassword().isEmpty()) {
                        // skip check
                    } else {
                        sendErrorResponse(response, 
                            request, 
                            HttpServletResponse.SC_UNAUTHORIZED, 
                            "Authentication failed!", 
                            "User token invalid!"
                        );
                        return;
                    }
                }
    
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
    
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
    
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Account authentication successful: " + userDetails.getUsername());
            }
    
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException e) {
            sendErrorResponse(response, 
                request, 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Network Error!", 
                e.getMessage()
            );
            return;
        }
    }

    private void sendErrorResponse(
        @NotNull HttpServletResponse response,
        @NotNull HttpServletRequest request,
        int statusCode,
        String error,
        String message
    ) throws IOException {

        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorResponse = new HashMap<>(); 

        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("status", statusCode);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", request.getRequestURI());

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}
