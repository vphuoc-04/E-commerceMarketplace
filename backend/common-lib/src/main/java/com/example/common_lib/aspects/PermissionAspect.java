package com.example.common_lib.aspects;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.common_lib.annotations.RequirePermission;
import com.example.common_lib.services.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class PermissionAspect {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JwtService jwtService;

    @Before("@annotation(requirePermission)")
    public void checkPermissions(JoinPoint joinPoint, RequirePermission requirePermission) {
        System.out.println("[PermissionAspect] --- Checking permission ---");
        // Lấy JWT từ header
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new AccessDeniedException("Access Denied: No JWT token found");
        }
        String token = bearerToken.substring(7);
        Long userId = Long.valueOf(jwtService.getUserIdFromJwt(token));
        System.out.println("[PermissionAspect] userId: " + userId);
        if (userId == null) {
            throw new AccessDeniedException("Access Denied: Cannot resolve userId");
        }
        // Chuẩn bị headers cho RestTemplate
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", bearerToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        // Gọi user-service lấy các userCatalogueId
        ResponseEntity<List> response = restTemplate.exchange(
            "http://localhost:8082/api/v1/user_catalogue_user/catalogues/" + userId,
            HttpMethod.GET,
            entity,
            List.class
        );
        List<Long> catalogueIds = response.getBody();
        System.out.println("[PermissionAspect] catalogueIds: " + catalogueIds);
        // Gọi permission-service lấy các permission theo catalogueIds
        HttpEntity<List<Long>> permEntity = new HttpEntity<>(catalogueIds, headers);
        ResponseEntity<List> permResponse = restTemplate.exchange(
            "http://localhost:8084/api/v1/user_catalogue_permission/by-catalogues",
            HttpMethod.POST,
            permEntity,
            List.class
        );
        List<String> permissions = permResponse.getBody();
        System.out.println("[PermissionAspect] permissions: " + permissions);

        String action = requirePermission.action();
        System.out.println("[PermissionAspect] action: " + action);
        
        System.out.println("[PermissionAspect] --- ACCESS GRANTED ---");
    }
} 