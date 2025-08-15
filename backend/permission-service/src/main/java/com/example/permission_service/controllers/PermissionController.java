package com.example.permission_service.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.common_lib.annotations.RequirePermission;
import com.example.common_lib.resources.ApiResource;
import com.example.common_lib.services.JwtService;
import com.example.permission_service.entities.Permission;
import com.example.permission_service.repositories.PermissionRepository;
import com.example.permission_service.requests.StoreRequest;
import com.example.permission_service.requests.UpdateRequest;
import com.example.permission_service.resources.PermissionResource;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/get_all")
    @RequirePermission(action = "permission:get_all")
    public ResponseEntity<?> getAll(HttpServletRequest request) {
        List<Permission> permissions = permissionRepository.findAll();
        List<PermissionResource> resources = permissions.stream().map(this::toResource).collect(Collectors.toList());
        ApiResource<List<PermissionResource>> response = ApiResource.ok(resources, "Permission data fetched successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get_page")
    public ResponseEntity<?> getPage(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Permission> permissionPage = permissionRepository.findAll(pageable);
        Page<PermissionResource> resourcePage = permissionPage.map(this::toResource);
        ApiResource<Page<PermissionResource>> response = ApiResource.ok(resourcePage, "Permission page fetched successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return permissionRepository.findById(id)
                .map(permission -> ResponseEntity.ok(ApiResource.ok(toResource(permission), "Permission fetched successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResource.error("NOT_FOUND", "Permission not found", HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/batch")
    public ResponseEntity<?> getBatch(@RequestParam("ids") List<Long> ids) {
        List<Long> validIds = permissionRepository.findAllById(ids)
            .stream().map(Permission::getId).toList();
        return ResponseEntity.ok(ApiResource.ok(validIds, "Fetched valid permission ids successfully"));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody StoreRequest request, @RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);
            Long addedBy = Long.valueOf(jwtService.getUserIdFromJwt(token));
            Permission permission = Permission.builder()
                    .name(request.getName())
                    .publish(request.getPublish())
                    .description(request.getDescription())
                    .addedBy(addedBy)
                    .build();
            Permission saved = permissionRepository.save(permission);
            ApiResource<PermissionResource> response = ApiResource.ok(toResource(saved), "Permission created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResource.message("Network error", HttpStatus.UNAUTHORIZED));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateRequest request, @RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);
            Long editedBy = Long.valueOf(jwtService.getUserIdFromJwt(token));
            Permission permission = permissionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Permission not found"));
            permission.setName(request.getName());
            permission.setPublish(request.getPublish());
            permission.setDescription(request.getDescription());
            permission.setEditedBy(editedBy);
            Permission updated = permissionRepository.save(permission);
            ApiResource<PermissionResource> response = ApiResource.ok(toResource(updated), "Permission updated successfully");
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResource.error("NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResource.error("INTERNAL_SERVER_ERROR", "Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            permissionRepository.deleteById(id);
            return ResponseEntity.ok(ApiResource.message("Permission deleted successfully", HttpStatus.OK));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResource.error("NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResource.error("INTERNAL_SERVER_ERROR", "Error", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private PermissionResource toResource(Permission permission) {
        return PermissionResource.builder()
                .id(permission.getId())
                .name(permission.getName())
                .publish(permission.getPublish())
                .description(permission.getDescription())
                .addedBy(permission.getAddedBy())
                .editedBy(permission.getEditedBy())
                .build();
    }
} 