package com.dxc.projectservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.projectservice.domain.model.entity.ProjectMetrics;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.ProjectMetricsEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectMetricsPersistenceMapper {

    public ProjectMetricsEntity toEntity(ProjectMetrics domain) {
        if (domain == null) return null;
        
        ProjectMetricsEntity entity = new ProjectMetricsEntity();
        entity.setId(domain.getId());
        entity.setTenantId(domain.getTenantId());
        entity.setProjectId(domain.getProjectId());
        entity.setTotalTasks(domain.getTotalTasks());
        entity.setCompletedTasks(domain.getCompletedTasks());
        entity.setOverdueTasks(domain.getOverdueTasks());
        entity.setVelocity(domain.getVelocity());
        entity.setEfficiencyScore(domain.getEfficiencyScore());
        entity.setCalculatedAt(domain.getCalculatedAt());
        return entity;
    }

    public ProjectMetrics toDomain(ProjectMetricsEntity entity) {
        if (entity == null) return null;
        
        return ProjectMetrics.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .projectId(entity.getProjectId())
                .totalTasks(entity.getTotalTasks())
                .completedTasks(entity.getCompletedTasks())
                .overdueTasks(entity.getOverdueTasks())
                .velocity(entity.getVelocity())
                .efficiencyScore(entity.getEfficiencyScore())
                .calculatedAt(entity.getCalculatedAt())
                .build();
    }
}
