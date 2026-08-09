package com.dxc.projectservice.infrastructure.adapter.in.rest.controller;

import com.dxc.projectservice.application.dto.dashboard.res.ProjectDashboardKpiResponse;
import com.dxc.projectservice.application.port.in.ProjectMetricsUseCase;
import com.dxc.projectservice.infrastructure.adapter.in.rest.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects/dashboard")
@RequiredArgsConstructor
public class ProjectDashboardController {

    private final ProjectMetricsUseCase projectMetricsUseCase;

    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<ProjectDashboardKpiResponse>> getDashboardKpis() {
        ProjectDashboardKpiResponse response = projectMetricsUseCase.getDashboardKpis();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
