package com.dxc.projectservice.application.port.in;

import com.dxc.projectservice.application.dto.dashboard.res.ProjectDashboardKpiResponse;


public interface ProjectMetricsUseCase {
    ProjectDashboardKpiResponse getDashboardKpis();
}
