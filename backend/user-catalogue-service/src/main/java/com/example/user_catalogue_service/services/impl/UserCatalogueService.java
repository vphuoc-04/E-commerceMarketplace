package com.example.user_catalogue_service.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common_lib.services.BaseService;
import com.example.user_catalogue_service.entities.UserCatalogue;
import com.example.user_catalogue_service.repositories.UserCatalogueRepository;
import com.example.user_catalogue_service.requests.StoreRequest;
import com.example.user_catalogue_service.requests.UpdateRequest;
import com.example.user_catalogue_service.services.interfaces.UserCatalogueServiceInterface;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserCatalogueService extends BaseService implements UserCatalogueServiceInterface {
    @Autowired
    private UserCatalogueRepository userCatalogueRepository;

    @Override
    public Page<UserCatalogue> paginate(Map<String, String[]> parameters) {
        int page = parameters.containsKey("page") ? Integer.parseInt(parameters.get("page")[0]) : 1;
        int perpage = parameters.containsKey("perpage") ? Integer.parseInt(parameters.get("perpage")[0]) : 10;
        String sortParam = parameters.containsKey("sort") ? parameters.get("sort")[0] : null;
        Sort sort = createSort(sortParam);

        Pageable pageable = PageRequest.of(page - 1, perpage, sort);

        return userCatalogueRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public UserCatalogue create(StoreRequest request, Long createdBy) {
        try {
            UserCatalogue payload = UserCatalogue.builder()
                .name(request.getName())
                .publish(request.getPublish())
                .createdBy(createdBy)
                .build();

            return userCatalogueRepository.save(payload);
        } catch (Exception e) {
            throw new RuntimeException("Transaction failed: " + e.getMessage());
        }
    }  

    @Override
    @Transactional
    public UserCatalogue update(Long id, UpdateRequest request, Long updatedBy) {
        UserCatalogue userCatalogue = userCatalogueRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User catalogue not found"));
        
        UserCatalogue payload = userCatalogue.toBuilder()
            .name(request.getName())
            .publish(request.getPublish())
            .updatedBy(updatedBy)
            .build();    

        return userCatalogueRepository.save(payload);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        UserCatalogue userCatalogue = userCatalogueRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User catalogue not found"));

        userCatalogueRepository.delete(userCatalogue);

        return true;
    }
}
