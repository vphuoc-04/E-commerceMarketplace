package com.example.permission_service.entities;

import java.io.Serializable;
import java.util.Objects;

public class UserCataloguePermissionId implements Serializable {
    private Long userCatalogueId;
    private Long permissionId;

    public UserCataloguePermissionId() {}
    public UserCataloguePermissionId(Long userCatalogueId, Long permissionId) {
        this.userCatalogueId = userCatalogueId;
        this.permissionId = permissionId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCataloguePermissionId that = (UserCataloguePermissionId) o;
        return Objects.equals(userCatalogueId, that.userCatalogueId) &&
               Objects.equals(permissionId, that.permissionId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(userCatalogueId, permissionId);
    }
} 