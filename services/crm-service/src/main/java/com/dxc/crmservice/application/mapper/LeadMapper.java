package com.dxc.crmservice.application.mapper;

import com.dxc.crmservice.application.dto.lead.req.CreateLeadRequest;
import com.dxc.crmservice.application.dto.lead.res.LeadResponse;
import com.dxc.crmservice.domain.model.aggregate.Lead;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface LeadMapper {

    default Lead toDomain(CreateLeadRequest request, UUID tenantId) {
        if (request == null) return null;
        return Lead.create(
                tenantId,
                request.title(),
                request.description(),
                request.source(),
                request.priority()
        );
    }

    LeadResponse toResponse(Lead lead);
}
