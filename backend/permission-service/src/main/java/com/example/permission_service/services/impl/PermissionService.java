package com.example.permission_service.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.permission_service.entities.Permission;
import com.example.permission_service.repositories.PermissionRepository;

@Service
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }

    public boolean userHasPermission(Long userId, String permissionName) {
        List<String> permissions = permissionRepository.findPermissionNamesByUserId(userId);
        return permissions.contains(permissionName);
    }
} 