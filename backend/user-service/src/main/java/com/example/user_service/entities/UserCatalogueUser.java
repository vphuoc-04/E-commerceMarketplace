package com.example.user_service.entities;

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
@Table(name = "user_catalogue_user")
@IdClass(UserCatalogueUserId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCatalogueUser implements Serializable {
    @Id
    @Column(name = "user_id")
    private Long userId;
    @Id
    @Column(name = "user_catalogue_id")
    private Long userCatalogueId;
} 