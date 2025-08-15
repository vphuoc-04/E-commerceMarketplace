package com.example.user_catalogue_service.services.interfaces;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.example.user_catalogue_service.entities.UserCatalogue;
import com.example.user_catalogue_service.requests.StoreRequest;
import com.example.user_catalogue_service.requests.UpdateRequest;

public interface UserCatalogueServiceInterface {
    Page<UserCatalogue> paginate(Map<String, String[]> parameters);
    UserCatalogue create(StoreRequest request, Long addedBy, String accessToken);
    UserCatalogue update(Long id, UpdateRequest request, Long editedBy, String accessToken);
    boolean delete(Long id);
}
