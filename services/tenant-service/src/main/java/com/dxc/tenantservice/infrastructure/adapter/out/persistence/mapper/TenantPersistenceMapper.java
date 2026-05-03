package com.dxc.tenantservice.infrastructure.adapter.out.persistence.mapper;

import com.dxc.tenantservice.domain.model.tenant.Tenant;
import com.dxc.tenantservice.domain.model.valueobject.Address;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity.AddressEmbeddable;
import com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity.TenantEntity;
import org.springframework.stereotype.Component;

@Component
public class TenantPersistenceMapper {

    private final TenantSettingsPersistenceMapper settingsMapper;

    public TenantPersistenceMapper(TenantSettingsPersistenceMapper settingsMapper) {
        this.settingsMapper = settingsMapper;
    }

    public TenantEntity domainToEntity(Tenant domain) {
        if (domain == null) return null;

        AddressEmbeddable addressEmbeddable = null;
        if (domain.getAddress() != null) {
            addressEmbeddable = AddressEmbeddable.builder()
                    .street(domain.getAddress().getStreet())
                    .city(domain.getAddress().getCity())
                    .state(domain.getAddress().getState())
                    .country(domain.getAddress().getCountry())
                    .zipCode(domain.getAddress().getZipCode())
                    .build();
        }

        return TenantEntity.builder()
                .id(domain.getId())
                .keycloakGroupId(domain.getKeycloakGroupId())
                .name(domain.getName())
                .email(domain.getEmail())
                .website(domain.getWebsite())
                .size(domain.getSize())
                .foundedDate(domain.getFoundedDate())
                .description(domain.getDescription())
                .logo(domain.getLogo())
                .address(addressEmbeddable)
                .industry(domain.getIndustry())
                .status(domain.getStatus())
                .settings(settingsMapper.domainToEntity(domain.getTenantSettings()))
                .build();
    }

    public Tenant entityToDomain(TenantEntity entity) {
        if (entity == null) return null;

        Address address = null;
        if (entity.getAddress() != null) {
            address = new Address(
                    entity.getAddress().getStreet(),
                    entity.getAddress().getCity(),
                    entity.getAddress().getState(),
                    entity.getAddress().getCountry(),
                    entity.getAddress().getZipCode()
            );
        }

        return new Tenant(
                entity.getId(),
                entity.getKeycloakGroupId(),
                entity.getName(),
                entity.getEmail(),
                entity.getWebsite(),
                entity.getSize(),
                entity.getFoundedDate(),
                entity.getDescription(),
                entity.getLogo(),
                address,
                entity.getIndustry(),
                entity.getStatus(),
                settingsMapper.entityToDomain(entity.getSettings())
        );
    }
}
