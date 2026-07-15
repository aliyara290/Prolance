package com.dxc.taskservice.application.port.in;

import com.dxc.taskservice.application.dto.projectdashboard.ProjectTaskDashboardFilterRequest;
import com.dxc.taskservice.application.dto.projectdashboard.ProjectTaskDashboardResponse;

import java.util.UUID;

public interface ProjectTaskDashboardUseCase {
    ProjectTaskDashboardResponse getProjectDashboard(UUID projectId, ProjectTaskDashboardFilterRequest filter);
}
