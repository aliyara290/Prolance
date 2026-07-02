package com.dxc.taskservice.infrastructure.adapter.in.rest.controller;

import com.dxc.taskservice.application.dto.assignment.req.AssignUserRequest;
import com.dxc.taskservice.application.dto.assignment.req.UpdateAssignmentRequest;
import com.dxc.taskservice.application.dto.assignment.res.TaskAssignmentResponse;
import com.dxc.taskservice.application.port.in.TaskAssignmentUseCase;
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
@RequestMapping("/api/v1/tasks/{taskId}/assignments")
public class TaskAssignmentController {

    private final TaskAssignmentUseCase assignmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskAssignmentResponse>> assignUser(
            @PathVariable UUID taskId,
            @Valid @RequestBody AssignUserRequest request) {
        TaskAssignmentResponse response = assignmentService.assignUser(taskId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<TaskAssignmentResponse>> updateAssignment(
            @PathVariable UUID taskId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateAssignmentRequest request) {
        TaskAssignmentResponse response = assignmentService.updateAssignment(taskId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unassignUser(
            @PathVariable UUID taskId,
            @PathVariable UUID userId) {
        assignmentService.unassignUser(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskAssignmentResponse>>> getAssignments(
            @PathVariable UUID taskId) {
        List<TaskAssignmentResponse> response = assignmentService.getAssignments(taskId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
