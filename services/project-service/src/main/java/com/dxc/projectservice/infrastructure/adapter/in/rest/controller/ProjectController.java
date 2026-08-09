package com.dxc.projectservice.infrastructure.adapter.in.rest.controller;

import com.dxc.projectservice.application.dto.project.req.CreateProjectRequest;
import com.dxc.projectservice.application.dto.project.req.UpdateProjectRequest;
import com.dxc.projectservice.application.dto.project.res.ProjectResponse;
import com.dxc.projectservice.application.dto.project.res.ProjectsNamesResponse;
import com.dxc.projectservice.application.port.in.ProjectUseCase;
import com.dxc.projectservice.infrastructure.adapter.in.rest.response.ApiResponse;
import com.dxc.projectservice.infrastructure.adapter.in.rest.response.PageMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectUseCase projectUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(@Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse response = projectUseCase.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(@PathVariable("id") UUID id) {
        ProjectResponse response = projectUseCase.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjects(Pageable pageable) {
        Page<ProjectResponse> response = projectUseCase.getProjects(pageable);
        PageMeta meta = PageMeta.builder()
                .totalPages(response.getTotalPages())
                .totalElements(response.getTotalElements())
                .size(response.getSize())
                .hasNext(response.hasNext())
                .hasPrevious(response.hasPrevious())
                .page(response.getNumber())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response.getContent(), meta));
    }

    @GetMapping("/names")
    public ResponseEntity<ApiResponse<List<ProjectsNamesResponse>>> getProjectsNames(Pageable pageable) {
        List<ProjectsNamesResponse> response = projectUseCase.getProjectsNames(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<ApiResponse<List<com.dxc.projectservice.application.dto.project.ProjectActivityDto>>> getProjectActivities(@PathVariable("id") UUID id) {
        List<com.dxc.projectservice.application.dto.project.ProjectActivityDto> response = projectUseCase.getProjectActivities(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateProjectRequest request) {
        ProjectResponse response = projectUseCase.updateProject(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") UUID id) {
        projectUseCase.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
