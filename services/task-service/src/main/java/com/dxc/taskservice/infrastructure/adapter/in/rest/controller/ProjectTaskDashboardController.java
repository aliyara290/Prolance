package com.dxc.taskservice.infrastructure.adapter.in.rest.controller;

import com.dxc.taskservice.application.dto.projectdashboard.ProjectTaskDashboardFilterRequest;
import com.dxc.taskservice.application.dto.projectdashboard.ProjectTaskDashboardResponse;
import com.dxc.taskservice.application.port.in.ProjectTaskDashboardUseCase;
import com.dxc.taskservice.domain.model.valueobject.TaskPriority;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;
import com.dxc.taskservice.infrastructure.adapter.in.rest.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks/project")
@RequiredArgsConstructor
public class ProjectTaskDashboardController {

    private final ProjectTaskDashboardUseCase dashboardUseCase;

    @GetMapping("/{projectId}/kpis")
    public ResponseEntity<ApiResponse<ProjectTaskDashboardResponse>> getProjectDashboard(
            @PathVariable UUID projectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID assigneeId,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) TaskStatus status) {

        ProjectTaskDashboardFilterRequest filter = new ProjectTaskDashboardFilterRequest(
                startDate, endDate, assigneeId, priority, status);

        ProjectTaskDashboardResponse response = dashboardUseCase.getProjectDashboard(projectId, filter);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
