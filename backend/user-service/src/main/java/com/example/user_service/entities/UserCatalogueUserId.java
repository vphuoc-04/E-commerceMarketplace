package com.example.user_service.entities;

import java.io.Serializable;
import java.util.Objects;

public class UserCatalogueUserId implements Serializable {
    private Long userId;
    private Long userCatalogueId;

    public UserCatalogueUserId() {}
    public UserCatalogueUserId(Long userId, Long userCatalogueId) {
        this.userId = userId;
        this.userCatalogueId = userCatalogueId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCatalogueUserId that = (UserCatalogueUserId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(userCatalogueId, that.userCatalogueId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(userId, userCatalogueId);
    }
} 