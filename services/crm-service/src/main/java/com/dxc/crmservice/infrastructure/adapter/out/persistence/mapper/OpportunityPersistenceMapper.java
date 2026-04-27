package com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.crmservice.domain.model.aggregate.Opportunity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.OpportunityEntity;
import org.springframework.stereotype.Component;

@Component
public class OpportunityPersistenceMapper {

    public OpportunityEntity toEntity(Opportunity domain) {
        if (domain == null) return null;
        OpportunityEntity entity = OpportunityEntity.builder()
                .id(domain.getId())
                .clientId(domain.getClientId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .estimatedBudget(domain.getEstimatedBudget())
                .expectedRevenue(domain.getExpectedRevenue())
                .probability(domain.getProbability())
                .expectedStartDate(domain.getExpectedStartDate())
                .expectedEndDate(domain.getExpectedEndDate())
                .closingDate(domain.getClosingDate())
                .stage(domain.getStage())
                .priority(domain.getPriority())
                .lastActivityAt(domain.getLastActivityAt())
                .nextFollowUpAt(domain.getNextFollowUpAt())
                .lostReason(domain.getLostReason())
                .build();
        
        entity.setTenantId(domain.getTenantId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        return entity;
    }

    public Opportunity toDomain(OpportunityEntity entity) {
        if (entity == null) return null;
        return Opportunity.rehydrate(
                entity.getId(),
                entity.getTenantId(),
                entity.getClientId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getEstimatedBudget(),
                entity.getExpectedRevenue(),
                entity.getProbability(),
                entity.getPriority(),
                entity.getExpectedStartDate(),
                entity.getExpectedEndDate(),
                entity.getClosingDate(),
                entity.getStage(),
                entity.getLastActivityAt(),
                entity.getNextFollowUpAt(),
                entity.getLostReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
