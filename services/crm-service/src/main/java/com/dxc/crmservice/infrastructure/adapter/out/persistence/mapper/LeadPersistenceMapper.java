package com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.crmservice.domain.model.aggregate.Lead;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.LeadEntity;
import org.springframework.stereotype.Component;

@Component
public class LeadPersistenceMapper {

    public LeadEntity toEntity(Lead domain) {
        if (domain == null) return null;
        LeadEntity entity = LeadEntity.builder()
                .id(domain.getId())
                .clientId(domain.getClientId())
                .contactId(domain.getContactId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .source(domain.getSource())
                .status(domain.getStatus())
                .score(domain.getScore())
                .priority(domain.getPriority())
                .assignedTo(domain.getAssignedTo())
                .firstContactedAt(domain.getFirstContactedAt())
                .lastActivityAt(domain.getLastActivityAt())
                .unqualifiedReason(domain.getUnqualifiedReason())
                .build();
        
        entity.setTenantId(domain.getTenantId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        return entity;
    }

    public Lead toDomain(LeadEntity entity) {
        if (entity == null) return null;
        return Lead.rehydrate(
                entity.getId(),
                entity.getTenantId(),
                entity.getClientId(),
                entity.getContactId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getSource(),
                entity.getStatus(),
                entity.getScore(),
                entity.getPriority(),
                entity.getAssignedTo(),
                entity.getFirstContactedAt(),
                entity.getLastActivityAt(),
                entity.getUnqualifiedReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
