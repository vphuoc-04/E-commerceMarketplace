package com.example.permission_service.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionResource {
    private final Long id;
    private final String name;
    private final Integer publish;
    private final String description;
    private final Long addedBy;
    private final Long editedBy;
}
