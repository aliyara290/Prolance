package com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.crmservice.domain.model.entity.Contact;
import com.dxc.crmservice.domain.model.valueobject.Address;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.AddressEmbeddable;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.ContactEntity;
import org.springframework.stereotype.Component;

@Component
public class ContactPersistenceMapper {

    public ContactEntity toEntity(Contact domain) {
        if (domain == null) return null;
        ContactEntity entity = ContactEntity.builder()
                .id(domain.getId())
                .clientId(domain.getClientId())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .email(domain.getEmail())
                .phone(domain.getPhone())
                .role(domain.getRole())
                .influenceLevel(domain.getInfluenceLevel())
                .primary(domain.isPrimary())
                .lastContactedAt(domain.getLastContactedAt())
                .notes(domain.getNotes())
                .department(domain.getDepartment())
                .dateOfBirth(domain.getDateOfBirth())
                .secondaryEmail(domain.getSecondaryEmail())
                .address(mapAddress(domain.getAddress()))
                .description(domain.getDescription())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
        
        entity.setTenantId(domain.getTenantId());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        return entity;
    }

    public Contact toDomain(ContactEntity entity) {
        if (entity == null) return null;
        return Contact.rehydrate(
                entity.getId(),
                entity.getTenantId(),
                entity.getClientId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getRole(),
                entity.getInfluenceLevel(),
                entity.isPrimary(),
                entity.getLastContactedAt(),
                entity.getNotes(),
                entity.getDepartment(),
                entity.getDateOfBirth(),
                entity.getSecondaryEmail(),
                mapAddressToDomain(entity.getAddress()),
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
