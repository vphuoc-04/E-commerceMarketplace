package com.example.user_catalogue_service.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.common_lib.annotations.RequirePermission;
import com.example.common_lib.resources.ApiResource;
import com.example.common_lib.services.JwtService;
import com.example.user_catalogue_service.entities.UserCatalogue;
import com.example.user_catalogue_service.repositories.UserCatalogueRepository;
import com.example.user_catalogue_service.requests.StoreRequest;
import com.example.user_catalogue_service.requests.UpdateRequest;
import com.example.user_catalogue_service.resources.UserCatalogueResource;
import com.example.user_catalogue_service.services.interfaces.UserCatalogueServiceInterface;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user_catalogue")
public class UserCatalogueController {
    private final UserCatalogueServiceInterface userCatagoluesService;

    @Autowired
    private UserCatalogueRepository userCatalogueRepository;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${permission.service.url:http://localhost:8084}")
    private String permissionServiceUrl;
    
    @Value("${user.service.url:http://localhost:8082}")
    private String userServiceUrl;

    public UserCatalogueController(
        UserCatalogueServiceInterface userCatagoluesService,
        JwtService jwtService
    ){
        this.userCatagoluesService = userCatagoluesService;
        this.jwtService = jwtService;
    }

    @GetMapping("/get_all_catalogue")
    @RequirePermission(action = "user_catalogue:get_all")
    public ResponseEntity<?> getAllCatalogues(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();

        Page<UserCatalogue> userCatalogues = userCatagoluesService.paginate(parameters);

        Page<UserCatalogueResource> userCatalogueResource = userCatalogues.map(userCatalogue -> 
            UserCatalogueResource.builder()
                .id(userCatalogue.getId())
                .addedBy(userCatalogue.getAddedBy())
                .editedBy(userCatalogue.getEditedBy())
                .name(userCatalogue.getName())
                .publish(userCatalogue.getPublish())
                .build()
        );

        ApiResource<Page<UserCatalogueResource>> response = ApiResource.ok(userCatalogueResource, "User catalogue data fetched successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create_catalogue")
    @RequirePermission(action = "user_catalogue:create")
    public ResponseEntity<?> createCatalogue(@RequestBody StoreRequest request, @RequestHeader("Authorization") String bearerToken) {
        String token = bearerToken.substring(7);
        String userId = jwtService.getUserIdFromJwt(token);
        Long addedBy = Long.valueOf(userId);
        UserCatalogue catalogue = UserCatalogue.builder()
            .name(request.getName())
            .publish(request.getPublish())
            .addedBy(addedBy)
            .build();
        userCatalogueRepository.save(catalogue);

        // Gán quyền cho catalogue
        String permUrl = permissionServiceUrl + "/api/v1/user_catalogue_permission";
        HttpHeaders permHeaders = new HttpHeaders();
        permHeaders.setContentType(MediaType.APPLICATION_JSON);
        permHeaders.set("Authorization", bearerToken);
        Map<String, Object> permBody = Map.of(
            "catalogueId", catalogue.getId(),
            "permissionIds", request.getPermissions()
        );
        HttpEntity<Map<String, Object>> permEntity = new HttpEntity<>(permBody, permHeaders);
        restTemplate.postForEntity(permUrl, permEntity, Void.class);

        // Gán user vào catalogue
        String userUrl = userServiceUrl + "/api/v1/user_catalogue_user";
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        userHeaders.set("Authorization", bearerToken);
        Map<String, Object> userBody = Map.of(
            "catalogueId", catalogue.getId(),
            "userIds", request.getUsers() 
        );
        HttpEntity<Map<String, Object>> userEntity = new HttpEntity<>(userBody, userHeaders);
        restTemplate.postForEntity(userUrl, userEntity, Void.class);

        UserCatalogueResource userCatalogueResource = UserCatalogueResource.builder()
            .id(catalogue.getId())
            .addedBy(catalogue.getAddedBy())
            .editedBy(catalogue.getEditedBy())
            .name(catalogue.getName())
            .publish(catalogue.getPublish())
            .build();

        ApiResource<UserCatalogueResource> response = ApiResource.ok(userCatalogueResource, "User catalogue created successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update_catalogue/{id}")
    @RequirePermission(action = "user_catalogue:update")
    public ResponseEntity<?> updateCatalogue(@PathVariable Long id, @Valid @RequestBody UpdateRequest request, @RequestHeader("Authorization") String bearerToken) {
        try {
            String token = bearerToken.substring(7);
            String userId = jwtService.getUserIdFromJwt(token);
            Long editedBy = Long.valueOf(userId);

            UserCatalogue userCatalogue = userCatalogueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User catalogue not found"));

            userCatalogue.setName(request.getName());
            userCatalogue.setPublish(request.getPublish());
            userCatalogue.setEditedBy(editedBy);
            userCatalogueRepository.save(userCatalogue);

            UserCatalogueResource userCatalogueResource = UserCatalogueResource.builder()
                .id(userCatalogue.getId())
                .addedBy(userCatalogue.getAddedBy())
                .editedBy(userCatalogue.getEditedBy())
                .name(userCatalogue.getName())
                .publish(userCatalogue.getPublish())
                .build();

            ApiResource<UserCatalogueResource> response = ApiResource.ok(userCatalogueResource, "User catalogue updated successfully");

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResource.error("NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResource.error("INTERNAL_SERVER_ERROR", "Error", HttpStatus.INTERNAL_SERVER_ERROR)
            );
        }   
    }

    @DeleteMapping("/delete_catalogue/{id}")
    @RequirePermission(action = "user_catalogue:delete")
    public ResponseEntity<?> deleteCatalogue(@PathVariable Long id) {
        try {
            // Xóa liên kết permission
            String permUrl = permissionServiceUrl + "/api/v1/user_catalogue_permission/delete/by-catalogue/" + id;
            restTemplate.delete(permUrl);
            // Xóa liên kết user
            String userUrl = userServiceUrl + "/api/v1/user_catalogue_user/delete/by-catalogue/" + id;
            restTemplate.delete(userUrl);
            // Xóa catalogue
            userCatalogueRepository.deleteById(id);
            return ResponseEntity.ok(
                ApiResource.message("User catalogue deleted successfully", HttpStatus.OK)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResource.error("NOT_FOUND", e.getMessage(), HttpStatus.NOT_FOUND)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResource.error("INTERNAL_SERVER_ERROR", "Error", HttpStatus.INTERNAL_SERVER_ERROR)
            );
        }   
    }
}