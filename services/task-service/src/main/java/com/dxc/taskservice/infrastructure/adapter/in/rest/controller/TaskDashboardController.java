package com.dxc.taskservice.infrastructure.adapter.in.rest.controller;

import com.dxc.taskservice.application.dto.dashboard.res.TaskDashboardKpiResponse;
import com.dxc.taskservice.application.port.in.TaskDashboardKpiUseCase;
import com.dxc.taskservice.infrastructure.adapter.in.rest.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks/dashboard")
@RequiredArgsConstructor
public class TaskDashboardController {

    private final TaskDashboardKpiUseCase taskDashboardKpiUseCase;

    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<TaskDashboardKpiResponse>> getDashboardKpis() {
        TaskDashboardKpiResponse response = taskDashboardKpiUseCase.getDashboardKpis();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}