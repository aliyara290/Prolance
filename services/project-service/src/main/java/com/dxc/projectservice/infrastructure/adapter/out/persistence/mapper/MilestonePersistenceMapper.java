package com.dxc.projectservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.projectservice.domain.model.entity.Milestone;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.MilestoneEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MilestonePersistenceMapper {

    public MilestoneEntity toEntity(Milestone domain) {
        if (domain == null) return null;
        
        MilestoneEntity entity = new MilestoneEntity();
        entity.setId(domain.getId());
        entity.setTenantId(domain.getTenantId());
        entity.setProjectId(domain.getProjectId());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setStartDate(domain.getStartDate());
        entity.setDueDate(domain.getDueDate());
        entity.setCompletedAt(domain.getCompletedAt());
        entity.setSequenceOrder(domain.getSequenceOrder());
        entity.setProgressPercentage(domain.getProgressPercentage());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public Milestone toDomain(MilestoneEntity entity) {
        if (entity == null) return null;
        
        return Milestone.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .projectId(entity.getProjectId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .dueDate(entity.getDueDate())
                .completedAt(entity.getCompletedAt())
                .sequenceOrder(entity.getSequenceOrder())
                .progressPercentage(entity.getProgressPercentage())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    public List<MilestoneEntity> toEntityList(List<Milestone> domains) {
        if (domains == null) return null;
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }
    
    public List<Milestone> toDomainList(List<MilestoneEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
