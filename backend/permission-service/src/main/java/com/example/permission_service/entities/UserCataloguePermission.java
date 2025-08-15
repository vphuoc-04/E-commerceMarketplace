package com.example.permission_service.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_catalogue_permission")
@IdClass(UserCataloguePermissionId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCataloguePermission implements Serializable {
    @Id
    @Column(name = "user_catalogue_id")
    private Long userCatalogueId;
    @Id
    @Column(name = "permission_id")
    private Long permissionId;
} 