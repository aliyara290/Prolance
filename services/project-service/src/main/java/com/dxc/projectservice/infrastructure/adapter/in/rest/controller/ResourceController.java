package com.dxc.projectservice.infrastructure.adapter.in.rest.controller;

import com.dxc.projectservice.application.dto.resource.req.AddResourceRequest;
import com.dxc.projectservice.application.dto.resource.res.ResourceResponse;
import com.dxc.projectservice.application.port.in.ResourceUseCase;
import com.dxc.projectservice.infrastructure.adapter.in.rest.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceUseCase resourceUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<ResourceResponse>> addResource(
            @PathVariable UUID projectId,
            @Valid @RequestBody AddResourceRequest request) {
        ResourceResponse response = resourceUseCase.addResource(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> removeResource(
            @PathVariable UUID projectId,
            @PathVariable UUID resourceId) {
        resourceUseCase.removeResource(projectId, resourceId);
        return ResponseEntity.noContent().build();
    }
}