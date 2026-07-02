package com.dxc.projectservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.projectservice.domain.model.aggregate.Project;
import com.dxc.projectservice.domain.model.entity.ProjectStatusHistory;
import com.dxc.projectservice.domain.model.valueobject.ProjectFinancials;
import com.dxc.projectservice.domain.model.valueobject.ProjectProgress;
import com.dxc.projectservice.domain.model.valueobject.ProjectTimeline;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.ProjectEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectPersistenceMapper {

    private final MemberPersistenceMapper memberMapper;
    private final MilestonePersistenceMapper milestoneMapper;
    private final ProjectResourcePersistenceMapper resourceMapper;
    private final ProjectMetricsPersistenceMapper metricsMapper;

    public ProjectEntity toEntity(Project domain) {
        if (domain == null) return null;

        ProjectEntity entity = new ProjectEntity();
        entity.setId(domain.getId());
        entity.setTenantId(domain.getTenantId());
        entity.setClientId(domain.getClientId());
        entity.setOpportunityId(domain.getOpportunityId());
        entity.setOwnerId(domain.getOwnerId());
        entity.setName(domain.getName());
        entity.setPrefix(domain.getPrefix());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setPriority(domain.getPriority());
        entity.setProjectManagerId(domain.getProjectManagerId());
        entity.setProgress(domain.getProgress() != null ? domain.getProgress().percentage() : 0);
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getTimeline() != null) {
            entity.setPlannedStartDate(domain.getTimeline().plannedStartDate());
            entity.setPlannedEndDate(domain.getTimeline().plannedEndDate());
            entity.setActualStartDate(domain.getTimeline().actualStartDate());
            entity.setActualEndDate(domain.getTimeline().actualEndDate());
        }

        if (domain.getFinancials() != null) {
            entity.setEstimatedBudget(domain.getFinancials().estimatedBudget());
            entity.setActualCost(domain.getFinancials().actualCost());
        }

        entity.setMembers(memberMapper.toEntityList(domain.getMembers()));
        entity.setMilestones(milestoneMapper.toEntityList(domain.getMilestones()));
        entity.setResources(resourceMapper.toEntityList(domain.getResources()));
        entity.setMetrics(metricsMapper.toEntity(domain.getMetrics()));
        
        if (domain.getStatusHistory() != null) {
            entity.setStatusHistory(domain.getStatusHistory().stream()
                    .map(this::toStatusHistoryEntity)
                    .collect(Collectors.toList()));
        }

        if (entity.getMembers() != null) entity.getMembers().forEach(m -> m.setProjectId(entity.getId()));
        if (entity.getMilestones() != null) entity.getMilestones().forEach(m -> m.setProjectId(entity.getId()));
        if (entity.getResources() != null) entity.getResources().forEach(m -> m.setProjectId(entity.getId()));
        if (entity.getMetrics() != null) entity.getMetrics().setProjectId(entity.getId());
        if (entity.getStatusHistory() != null) entity.getStatusHistory().forEach(m -> m.setProjectId(entity.getId()));

        return entity;
    }

    public Project toDomain(ProjectEntity entity) {
        if (entity == null) return null;

        ProjectTimeline timeline = null;
        if (entity.getPlannedStartDate() != null) {
            timeline = new ProjectTimeline(
                    entity.getPlannedStartDate(),
                    entity.getPlannedEndDate(),
                    entity.getActualStartDate(),
                    entity.getActualEndDate()
            );
        }

        ProjectFinancials financials = null;
        if (entity.getEstimatedBudget() != null && entity.getActualCost() != null) {
            financials = new ProjectFinancials(
                    entity.getEstimatedBudget(),
                    entity.getActualCost()
            );
        }

        ProjectProgress progress = new ProjectProgress(entity.getProgress());

        return Project.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .clientId(entity.getClientId())
                .ownerId(entity.getOwnerId())
                .opportunityId(entity.getOpportunityId())
                .name(entity.getName())
                .prefix(entity.getPrefix())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .timeline(timeline)
                .financials(financials)
                .progress(progress)
                .projectManagerId(entity.getProjectManagerId())
                .members(memberMapper.toDomainList(entity.getMembers()))
                .milestones(milestoneMapper.toDomainList(entity.getMilestones()))
                .resources(resourceMapper.toDomainList(entity.getResources()))
                .metrics(metricsMapper.toDomain(entity.getMetrics()))
                .statusHistory(
                        entity.getStatusHistory() != null ?
                                entity.getStatusHistory().stream()
                                        .map(this::toStatusHistoryDomain)
                                        .collect(Collectors.toList())
                                : null
                )
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    private com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.ProjectStatusHistory toStatusHistoryEntity(ProjectStatusHistory domain) {
        if (domain == null) return null;
        com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.ProjectStatusHistory entity =
                new com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.ProjectStatusHistory();
        entity.setId(domain.getId());
        entity.setTenantId(domain.getTenantId());
        entity.setProjectId(domain.getProjectId());
        entity.setOldStatus(domain.getOldStatus());
        entity.setNewStatus(domain.getNewStatus());
        entity.setChangedBy(domain.getChangedBy());
        entity.setChangedAt(domain.getChangedAt());
        entity.setComment(domain.getComment());
        return entity;
    }

    private ProjectStatusHistory toStatusHistoryDomain(com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.ProjectStatusHistory entity) {
        if (entity == null) return null;
        return ProjectStatusHistory.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .projectId(entity.getProjectId())
                .oldStatus(entity.getOldStatus())
                .newStatus(entity.getNewStatus())
                .changedBy(entity.getChangedBy())
                .changedAt(entity.getChangedAt())
                .comment(entity.getComment())
                .build();
    }
}
