package com.example.permission_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.permission_service.entities.UserCataloguePermission;
import com.example.permission_service.entities.UserCataloguePermissionId;

public interface UserCataloguePermissionRepository extends JpaRepository<UserCataloguePermission, UserCataloguePermissionId> {
    @Query(value = "SELECT p.name FROM permissions p " +
            "JOIN user_catalogue_permission ucp ON p.id = ucp.permission_id " +
            "WHERE ucp.user_catalogue_id IN :userCatalogueIds", nativeQuery = true)
    List<String> findPermissionNamesByUserCatalogueIds(@Param("userCatalogueIds") List<Long> userCatalogueIds);

    void deleteByUserCatalogueId(Long userCatalogueId);

    List<UserCataloguePermission> findByUserCatalogueId(Long userCatalogueId);
} 