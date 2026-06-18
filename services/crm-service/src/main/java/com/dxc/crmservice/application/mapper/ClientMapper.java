package com.dxc.crmservice.application.mapper;

import com.dxc.crmservice.application.dto.AddressDto;
import com.dxc.crmservice.application.dto.client.req.CreateClientRequest;
import com.dxc.crmservice.application.dto.client.res.ClientResponse;
import com.dxc.crmservice.domain.model.aggregate.Client;
import com.dxc.crmservice.domain.model.valueobject.Address;
import org.mapstruct.Mapper;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    default Client toDomain(CreateClientRequest request, UUID tenantId) {
        if (request == null) return null;
        return Client.create(
                tenantId,
                request.name(),
                request.industry(),
                request.website(),
                request.phone(),
                toAddress(request.address()),
                request.type(),
                request.source(),
                request.annualRevenue(),
                request.fax(),
                request.ownership(),
                request.sicCode(),
                request.description(),
                null // createdBy - resolved from security context if needed
        );
    }

    default ClientResponse toResponse(Client client) {
        if (client == null) return null;
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getIndustry(),
                client.getWebsite(),
                client.getPhone(),
                toAddressDto(client.getAddress()),
                client.getAddress() != null ? client.getAddress().getCountry() : null,
                client.getStatus(),
                client.getType(),
                client.getSource(),
                client.getAnnualRevenue(),
                client.getFax(),
                client.getOwnership(),
                client.getSicCode(),
                client.getDescription(),
                client.getCreatedBy(),
                client.getUpdatedBy(),
                client.getCreatedAt(),
                client.getUpdatedAt()
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
