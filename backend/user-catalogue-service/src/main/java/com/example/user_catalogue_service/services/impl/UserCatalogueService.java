package com.example.user_catalogue_service.services.impl;

import java.util.List;
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

import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserCatalogueService extends BaseService implements UserCatalogueServiceInterface {
    @Autowired
    private UserCatalogueRepository userCatalogueRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${permission.service.url:http://localhost:8084}")
    private String permissionServiceUrl;
    @Value("${user.service.url:http://localhost:8082}")
    private String userServiceUrl;

    private List<Long> validatePermissions(List<Long> ids, String accessToken) {
        try {
            String url = permissionServiceUrl + "/api/v1/permissions/batch?ids=" + ids.stream().map(String::valueOf).collect(Collectors.joining(","));
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", accessToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            JsonNode dataNode = response.getBody().get("data");
            if (dataNode != null && dataNode.isArray()) {
                return objectMapper.convertValue(dataNode, new com.fasterxml.jackson.core.type.TypeReference<List<Long>>(){});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }
    private List<Long> validateUsers(List<Long> ids, String accessToken) {
        try {
            String url = userServiceUrl + "/api/v1/users/batch?ids=" + ids.stream().map(String::valueOf).collect(Collectors.joining(","));
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", accessToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            JsonNode dataNode = response.getBody().get("data");
            if (dataNode != null && dataNode.isArray()) {
                return objectMapper.convertValue(dataNode, new com.fasterxml.jackson.core.type.TypeReference<List<Long>>(){});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    private void assignPermissionsToCatalogue(Long catalogueId, List<Long> permissionIds, String accessToken) {
        try {
            String url = permissionServiceUrl + "/api/v1/user_catalogue_permission";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);
            Map<String, Object> body = Map.of(
                "catalogueId", catalogueId,
                "permissionIds", permissionIds
            );
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, entity, Void.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void assignUsersToCatalogue(Long catalogueId, List<Long> userIds, String accessToken) {
        try {
            String url = userServiceUrl + "/api/v1/user_catalogue_user";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);
            Map<String, Object> body = Map.of(
                "catalogueId", catalogueId,
                "userIds", userIds
            );
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, entity, Void.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    public UserCatalogue create(StoreRequest request, Long addedBy, String accessToken) {
        try {
            List<Long> validPermissionIds = validatePermissions(request.getPermissions(), accessToken);
            List<Long> validUserIds = validateUsers(request.getUsers(), accessToken);
            UserCatalogue payload = UserCatalogue.builder()
                .name(request.getName())
                .publish(request.getPublish())
                .addedBy(addedBy)
                .build();
            UserCatalogue saved = userCatalogueRepository.save(payload);
            // Gán quyền và user vào catalogue qua API
            assignPermissionsToCatalogue(saved.getId(), validPermissionIds, accessToken);
            assignUsersToCatalogue(saved.getId(), validUserIds, accessToken);
            return saved;
        } catch (Exception e) {
            throw new RuntimeException("Transaction failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserCatalogue update(Long id, UpdateRequest request, Long editedBy, String accessToken) {
        UserCatalogue userCatalogue = userCatalogueRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User catalogue not found"));
        List<Long> validPermissionIds = validatePermissions(request.getPermissions(), accessToken);
        List<Long> validUserIds = validateUsers(request.getUsers(), accessToken);
        userCatalogue.setName(request.getName());
        userCatalogue.setPublish(request.getPublish());
        userCatalogue.setEditedBy(editedBy);
        // userCatalogue.setPermissionIds(new HashSet<>(validPermissionIds));
        // userCatalogue.setUserIds(new HashSet<>(validUserIds));
        return userCatalogueRepository.save(userCatalogue);
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
