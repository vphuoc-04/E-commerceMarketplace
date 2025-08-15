package com.example.permission_service.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.permission_service.entities.UserCataloguePermission;
import com.example.permission_service.repositories.UserCataloguePermissionRepository;

@RestController
@RequestMapping("/api/v1/user_catalogue_permission")
public class UserCataloguePermissionController {
    @Autowired
    private UserCataloguePermissionRepository userCataloguePermissionRepository;

    @PostMapping
    public ResponseEntity<?> assignPermissions(@RequestBody Map<String, Object> request) {
        Long userCatalogueId = Long.valueOf(request.get("catalogueId").toString());
        List<Integer> permissionIds = (List<Integer>) request.get("permissionIds");
        for (Integer permissionId : permissionIds) {
            userCataloguePermissionRepository.save(new UserCataloguePermission(userCatalogueId, permissionId.longValue()));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/by-catalogues")
    public List<String> getPermissionsByCatalogueIds(@RequestBody List<Long> catalogueIds) {
        return userCataloguePermissionRepository.findPermissionNamesByUserCatalogueIds(catalogueIds);
    }

    @GetMapping("/by-catalogue/{userCatalogueId}")
    public List<Long> getPermissionIdsByUserCatalogueId(@PathVariable Long userCatalogueId) {
        return userCataloguePermissionRepository.findByUserCatalogueId(userCatalogueId)
            .stream().map(ucp -> ucp.getPermissionId()).toList();
    }

    @Transactional
    @DeleteMapping("/delete/by-catalogue/{userCatalogueId}")
    public ResponseEntity<?> deleteByUserCatalogueId(@PathVariable Long userCatalogueId) {
        userCataloguePermissionRepository.deleteByUserCatalogueId(userCatalogueId);
        return ResponseEntity.ok().build();
    }
} 