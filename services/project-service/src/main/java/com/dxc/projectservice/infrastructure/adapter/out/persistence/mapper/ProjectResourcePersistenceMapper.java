package com.dxc.projectservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.projectservice.domain.model.entity.ProjectResource;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.ProjectResourceEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectResourcePersistenceMapper {

    public ProjectResourceEntity toEntity(ProjectResource domain) {
        if (domain == null) return null;
        
        ProjectResourceEntity entity = new ProjectResourceEntity();
        entity.setId(domain.getId());
        entity.setTenantId(domain.getTenantId());
        entity.setProjectId(domain.getProjectId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setType(domain.getType());
        entity.setUrl(domain.getUrl());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    public ProjectResource toDomain(ProjectResourceEntity entity) {
        if (entity == null) return null;
        
        return ProjectResource.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .projectId(entity.getProjectId())
                .name(entity.getName())
                .description(entity.getDescription())
                .type(entity.getType())
                .url(entity.getUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<ProjectResourceEntity> toEntityList(List<ProjectResource> domains) {
        if (domains == null) return null;
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<ProjectResource> toDomainList(List<ProjectResourceEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
