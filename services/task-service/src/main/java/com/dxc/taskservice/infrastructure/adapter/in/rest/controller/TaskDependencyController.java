package com.dxc.taskservice.infrastructure.adapter.in.rest.controller;

import com.dxc.taskservice.application.dto.dependency.req.AddDependencyRequest;
import com.dxc.taskservice.application.dto.dependency.res.TaskDependencyResponse;
import com.dxc.taskservice.application.port.in.TaskDependencyUseCase;
import com.dxc.taskservice.infrastructure.adapter.in.rest.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/tasks/{taskId}/dependencies")
public class TaskDependencyController {

    private final TaskDependencyUseCase dependencyService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskDependencyResponse>> addDependency(
            @PathVariable UUID taskId,
            @Valid @RequestBody AddDependencyRequest request) {
        TaskDependencyResponse response = dependencyService.addDependency(taskId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @DeleteMapping("/{dependencyId}")
    public ResponseEntity<Void> removeDependency(
            @PathVariable UUID taskId,
            @PathVariable UUID dependencyId) {
        dependencyService.removeDependency(taskId, dependencyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskDependencyResponse>>> getDependencies(
            @PathVariable UUID taskId) {
        List<TaskDependencyResponse> response = dependencyService.getDependencies(taskId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
