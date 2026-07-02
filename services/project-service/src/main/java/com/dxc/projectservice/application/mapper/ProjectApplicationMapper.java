package com.dxc.projectservice.application.mapper;

import com.dxc.projectservice.application.dto.project.res.ProjectResponse;
import com.dxc.projectservice.domain.model.aggregate.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectApplicationMapper {

    public ProjectResponse toResponse(Project project) {
        if (project == null) return null;

        return new ProjectResponse(
            project.getId(),
            project.getClientId(),
            project.getOwnerId(),
            project.getOpportunityId(),
            project.getName(),
            project.getDescription(),
            project.getPrefix(),
            project.getStatus(),
            project.getPriority(),
            project.getTimeline() != null ? project.getTimeline().plannedStartDate() : null,
            project.getTimeline() != null ? project.getTimeline().plannedEndDate() : null,
            project.getTimeline() != null ? project.getTimeline().actualStartDate() : null,
            project.getTimeline() != null ? project.getTimeline().actualEndDate() : null,
            project.getFinancials() != null ? project.getFinancials().estimatedBudget() : null,
            project.getFinancials() != null ? project.getFinancials().actualCost() : null,
            project.getProgress() != null ? project.getProgress().percentage() : 0,
            project.getProjectManagerId(),
            project.getCreatedAt(),
            project.getUpdatedAt()
        );
    }

    public List<ProjectResponse> toResponseList(List<Project> projects) {
        if (projects == null) return null;
        return projects.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
