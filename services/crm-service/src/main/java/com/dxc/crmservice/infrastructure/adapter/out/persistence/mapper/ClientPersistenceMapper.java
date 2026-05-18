package com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.crmservice.domain.model.aggregate.Client;
import com.dxc.crmservice.domain.model.valueobject.Address;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.AddressEmbeddable;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.ClientEntity;
import org.springframework.stereotype.Component;

@Component
public class ClientPersistenceMapper {

    public ClientEntity toEntity(Client domain) {
        if (domain == null) return null;
        ClientEntity entity = ClientEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .industry(domain.getIndustry())
                .website(domain.getWebsite())
                .phone(domain.getPhone())
                .address(mapAddress(domain.getAddress()))
                .status(domain.getStatus())
                .type(domain.getType())
                .source(domain.getSource())
                .annualRevenue(domain.getAnnualRevenue())
                .fax(domain.getFax())
                .ownership(domain.getOwnership())
                .sicCode(domain.getSicCode())
                .description(domain.getDescription())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
        
        entity.setTenantId(domain.getTenantId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        return entity;
    }

    public Client toDomain(ClientEntity entity) {
        if (entity == null) return null;
        return Client.rehydrate(
                entity.getId(),
                entity.getTenantId(),
                entity.getName(),
                entity.getIndustry(),
                entity.getWebsite(),
                entity.getPhone(),
                mapAddressToDomain(entity.getAddress()),
                entity.getStatus(),
                entity.getType(),
                entity.getSource(),
                entity.getAnnualRevenue(),
                entity.getFax(),
                entity.getOwnership(),
                entity.getSicCode(),
                entity.getDescription(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private AddressEmbeddable mapAddress(Address address) {
        if (address == null) return null;
        return AddressEmbeddable.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .country(address.getCountry())
                .build();
    }

    private Address mapAddressToDomain(AddressEmbeddable embeddable) {
        if (embeddable == null) return null;
        return Address.builder()
                .street(embeddable.getStreet())
                .city(embeddable.getCity())
                .state(embeddable.getState())
                .zipCode(embeddable.getZipCode())
                .country(embeddable.getCountry())
                .build();
    }
}
