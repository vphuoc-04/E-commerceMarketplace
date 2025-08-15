package com.example.user_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.user_service.entities.UserCatalogueUser;
import com.example.user_service.entities.UserCatalogueUserId;

public interface UserCatalogueUserRepository extends JpaRepository<UserCatalogueUser, UserCatalogueUserId> {
    @Query("SELECT u.userCatalogueId FROM UserCatalogueUser u WHERE u.userId = :userId")
    List<Long> findUserCatalogueIdsByUserId(@Param("userId") Long userId);

    void deleteByUserCatalogueId(Long userCatalogueId);

    List<UserCatalogueUser> findByUserCatalogueId(Long userCatalogueId);
} 