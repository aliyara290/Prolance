package com.dxc.taskservice.infrastructure.adapter.in.rest.controller;

import com.dxc.taskservice.application.dto.task.req.ChangeTaskStatusRequest;
import com.dxc.taskservice.application.dto.task.req.CreateTaskRequest;
import com.dxc.taskservice.application.dto.task.req.UpdateTaskRequest;
import com.dxc.taskservice.application.dto.task.res.TaskResponse;
import com.dxc.taskservice.application.port.in.TaskUseCase;
import com.dxc.taskservice.infrastructure.adapter.in.rest.response.ApiResponse;
import com.dxc.taskservice.infrastructure.adapter.in.rest.response.PageMeta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskUseCase taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody CreateTaskRequest taskRequest) {
        TaskResponse taskResponse = taskService.createTask(taskRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(taskResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse taskResponse = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success(taskResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(@PathVariable UUID id) {
        TaskResponse taskResponse = taskService.getTask(id);
        return ResponseEntity.ok(ApiResponse.success(taskResponse));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> changeTaskStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeTaskStatusRequest request) {
        TaskResponse taskResponse = taskService.changeTaskStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(taskResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasks(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TaskResponse> page = taskService.getTasks(pageable);
        return ResponseEntity.ok(ApiResponse.success(page.getContent(), toPageMeta(page)));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByProject(
            @PathVariable UUID projectId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TaskResponse> page = taskService.getTasksByProjectId(projectId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page.getContent(), toPageMeta(page)));
    }

    @GetMapping("/milestone/{milestoneId}")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByMilestone(
            @PathVariable UUID milestoneId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TaskResponse> page = taskService.getTasksByMilestoneId(milestoneId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page.getContent(), toPageMeta(page)));
    }

    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByAssignee(
            @PathVariable UUID assigneeId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TaskResponse> page = taskService.getTasksByAssigneeId(assigneeId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page.getContent(), toPageMeta(page)));
    }

    private <T> PageMeta toPageMeta(Page<T> page) {
        return PageMeta.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
