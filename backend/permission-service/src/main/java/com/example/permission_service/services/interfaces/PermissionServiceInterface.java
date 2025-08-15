package com.example.permission_service.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.example.permission_service.entities.Permission;

public interface PermissionServiceInterface {
    List<Permission> findAll();
    Optional<Permission> findById(Long id);
    Permission save(Permission permission);
}
