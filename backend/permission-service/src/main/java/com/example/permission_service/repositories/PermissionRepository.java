package com.example.permission_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.permission_service.entities.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    @Query(value = "SELECT p.name FROM permissions p " +
            "JOIN user_catalogue_permission ucp ON p.id = ucp.permission_id " +
            "JOIN user_catalogue_user ucu ON ucp.user_catalogue_id = ucu.user_catalogue_id " +
            "WHERE ucu.user_id = :userId", nativeQuery = true)
    List<String> findPermissionNamesByUserId(@Param("userId") Long userId);
} 