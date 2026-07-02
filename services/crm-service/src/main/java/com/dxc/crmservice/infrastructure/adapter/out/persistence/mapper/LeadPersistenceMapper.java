package com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.crmservice.domain.model.aggregate.Lead;
import com.dxc.crmservice.domain.model.valueobject.Address;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.AddressEmbeddable;
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
                .phone(domain.getPhone())
                .industry(domain.getIndustry())
                .annualRevenue(domain.getAnnualRevenue())
                .company(domain.getCompany())
                .email(domain.getEmail())
                .website(domain.getWebsite())
                .numberOfEmployees(domain.getNumberOfEmployees())
                .address(mapAddress(domain.getAddress()))
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
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
                entity.getPhone(),
                entity.getIndustry(),
                entity.getAnnualRevenue(),
                entity.getCompany(),
                entity.getEmail(),
                entity.getWebsite(),
                entity.getNumberOfEmployees(),
                mapAddressToDomain(entity.getAddress()),
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
