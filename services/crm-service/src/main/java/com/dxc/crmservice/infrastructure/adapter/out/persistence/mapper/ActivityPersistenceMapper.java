package com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.crmservice.domain.model.entity.Activity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.ActivityEntity;
import org.springframework.stereotype.Component;

@Component
public class ActivityPersistenceMapper {

    public ActivityEntity toEntity(Activity domain) {
        if (domain == null) return null;
        ActivityEntity entity = ActivityEntity.builder()
                .id(domain.getId())
                .type(domain.getType())
                .subject(domain.getSubject())
                .description(domain.getDescription())
                .scheduledAt(domain.getScheduledAt())
                .completedAt(domain.getCompletedAt())
                .userId(domain.getUserId())
                .entityId(domain.getEntityId())
                .entityType(domain.getEntityType())
                .build();
        
        entity.setTenantId(domain.getTenantId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        return entity;
    }

    public Activity toDomain(ActivityEntity entity) {
        if (entity == null) return null;
        return Activity.rehydrate(
                entity.getId(),
                entity.getTenantId(),
                entity.getType(),
                entity.getSubject(),
                entity.getDescription(),
                entity.getScheduledAt(),
                entity.getCompletedAt(),
                entity.getUserId(),
                entity.getEntityId(),
                entity.getEntityType(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
