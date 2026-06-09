package com.dxc.projectservice.application.mapper;

import com.dxc.projectservice.application.dto.metrics.res.ProjectMetricsResponse;
import com.dxc.projectservice.domain.model.entity.ProjectMetrics;
import org.springframework.stereotype.Component;

@Component
public class ProjectMetricsApplicationMapper {

    public ProjectMetricsResponse toResponse(ProjectMetrics metrics) {
        if (metrics == null) return null;
        
        return new ProjectMetricsResponse(
            metrics.getId(),
            metrics.getProjectId(),
            metrics.getTotalTasks(),
            metrics.getCompletedTasks(),
            metrics.getOverdueTasks(),
            metrics.getVelocity(),
            metrics.getEfficiencyScore(),
            metrics.getCalculatedAt()
        );
    }
}
