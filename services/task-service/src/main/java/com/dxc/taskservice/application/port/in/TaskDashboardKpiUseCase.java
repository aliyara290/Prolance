package com.dxc.taskservice.application.port.in;

import com.dxc.taskservice.application.dto.dashboard.res.TaskDashboardKpiResponse;


public interface TaskDashboardKpiUseCase {
    TaskDashboardKpiResponse getDashboardKpis();
}
