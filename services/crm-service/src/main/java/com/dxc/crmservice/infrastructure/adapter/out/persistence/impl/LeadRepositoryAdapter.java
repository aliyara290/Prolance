package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.LeadRepository;
import com.dxc.crmservice.domain.model.aggregate.Lead;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.LeadEntity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa.LeadRepositoryJpa;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper.LeadPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class LeadRepositoryAdapter implements LeadRepository {

    private final LeadRepositoryJpa leadRepositoryJpa;
    private final LeadPersistenceMapper leadPersistenceMapper;


    @Override
    public Lead save(Lead lead) {
        LeadEntity leadEntity = leadPersistenceMapper.toEntity(lead);
        LeadEntity savedLeadEntity = leadRepositoryJpa.save(leadEntity);
        return leadPersistenceMapper.toDomain(savedLeadEntity);
    }

    @Override
    public Lead findById(UUID id, UUID tenantId) {
        Optional<LeadEntity> leadEntity = leadRepositoryJpa.findByIdAndTenantId(id, tenantId);
        return leadEntity.map(leadPersistenceMapper::toDomain).orElse(null);
    }

    @Override
    public Lead update(Lead lead) {
        LeadEntity leadEntity = leadPersistenceMapper.toEntity(lead);
        LeadEntity updatedLeadEntity = leadRepositoryJpa.save(leadEntity);
        return leadPersistenceMapper.toDomain(updatedLeadEntity);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        leadRepositoryJpa.findByIdAndTenantId(id, tenantId).ifPresent(leadRepositoryJpa::delete);
    }

    @Override
    public Page<Lead> findAll(UUID tenantId, Pageable pageable) {
        Page<LeadEntity> leadEntities = leadRepositoryJpa.findByTenantId(tenantId, pageable);
        return leadEntities.map(leadPersistenceMapper::toDomain);
    }
}
