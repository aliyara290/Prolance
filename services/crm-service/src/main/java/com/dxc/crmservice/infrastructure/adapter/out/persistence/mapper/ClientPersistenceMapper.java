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
                .build();
    }

    private Address mapAddressToDomain(AddressEmbeddable embeddable) {
        if (embeddable == null) return null;
        return Address.builder()
                .street(embeddable.getStreet())
                .city(embeddable.getCity())
                .state(embeddable.getState())
                .zipCode(embeddable.getZipCode())
                .build();
    }
}
