package com.example.user_catalogue_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.user_catalogue_service.entities.UserCatalogue;

@Repository
public interface UserCatalogueRepository extends JpaRepository<UserCatalogue, Long> {
    Optional<UserCatalogue> findByName(String name);
}
