package com.dxc.crmservice.application.mapper;

import com.dxc.crmservice.application.dto.AddressDto;
import com.dxc.crmservice.application.dto.lead.req.CreateLeadRequest;
import com.dxc.crmservice.application.dto.lead.res.LeadResponse;
import com.dxc.crmservice.domain.model.aggregate.Lead;
import com.dxc.crmservice.domain.model.valueobject.Address;
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
                request.priority(),
                request.phone(),
                request.industry(),
                request.annualRevenue(),
                request.company(),
                request.email(),
                request.website(),
                request.numberOfEmployees(),
                toAddress(request.address()),
                null // createdBy
        );
    }

    default LeadResponse toResponse(Lead lead) {
        if (lead == null) return null;
        return new LeadResponse(
                lead.getId(),
                lead.getTitle(),
                lead.getDescription(),
                lead.getSource(),
                lead.getPriority(),
                lead.getStatus(),
                lead.getClientId(),
                lead.getContactId(),
                lead.getAssignedTo(),
                null, // contact - populated in service
                null, // client - populated in service
                lead.getPhone(),
                lead.getIndustry(),
                lead.getAnnualRevenue(),
                lead.getCompany(),
                lead.getEmail(),
                lead.getWebsite(),
                lead.getNumberOfEmployees(),
                toAddressDto(lead.getAddress()),
                lead.getCreatedBy(),
                lead.getUpdatedBy(),
                lead.getCreatedAt(),
                lead.getUpdatedAt()
        );
    }

    default Address toAddress(AddressDto dto) {
        if (dto == null) return null;
        return Address.builder()
                .street(dto.street())
                .city(dto.city())
                .state(dto.state())
                .zipCode(dto.zipCode())
                .country(dto.country())
                .build();
    }

    default AddressDto toAddressDto(Address address) {
        if (address == null) return null;
        return new AddressDto(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry()
        );
    }
}
