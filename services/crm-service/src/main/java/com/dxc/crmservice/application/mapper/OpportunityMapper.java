package com.dxc.crmservice.application.mapper;

import com.dxc.crmservice.application.dto.opportunity.req.CreateOpportunityRequest;
import com.dxc.crmservice.application.dto.opportunity.res.OpportunityResponse;
import com.dxc.crmservice.domain.model.aggregate.Opportunity;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OpportunityMapper {

    default Opportunity toDomain(CreateOpportunityRequest request, UUID tenantId) {
        if (request == null) return null;
        
        Double budget = request.estimatedBudget() != null ? request.estimatedBudget().doubleValue() : null;

        return Opportunity.create(
                tenantId,
                request.clientId(),
                request.title(),
                "", // description
                budget,
                request.expectedRevenue() != null ? request.expectedRevenue() : null, // expectedRevenue
                request.probability() > 0 ? request.probability() : 0,  // probability
                request.stage(),
                request.priority()
        );
    }

    default OpportunityResponse toResponse(Opportunity opportunity) {
        if (opportunity == null) return null;
        
        java.math.BigDecimal amount = opportunity.getEstimatedBudget() != null 
            ? java.math.BigDecimal.valueOf(opportunity.getEstimatedBudget()) 
            : null;

        return new OpportunityResponse(
                opportunity.getId(),
                opportunity.getClientId(),
                opportunity.getTitle(),
                amount,
                opportunity.getStage(),
                opportunity.getPriority(),
                opportunity.getEstimatedBudget(),
                opportunity.getExpectedRevenue(),
                opportunity.getProbability(),
                opportunity.getExpectedStartDate(),
                opportunity.getExpectedEndDate(),
                opportunity.getLastActivityAt(),
                opportunity.getNextFollowUpAt(),
                opportunity.getClosingDate(),
                opportunity.getLostReason(),
                opportunity.getCreatedAt(),
                opportunity.getUpdatedAt()
        );
    }
}
