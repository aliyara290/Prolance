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
        
        Double budget = request.amount() != null ? request.amount().doubleValue() : null;
        
        return Opportunity.create(
                tenantId,
                request.clientId(),
                request.name(),
                "", // description
                budget,
                0.0, // expectedRevenue
                0,   // probability
                request.stage(),
                request.priority()
        );
    }

    OpportunityResponse toResponse(Opportunity opportunity);
}
