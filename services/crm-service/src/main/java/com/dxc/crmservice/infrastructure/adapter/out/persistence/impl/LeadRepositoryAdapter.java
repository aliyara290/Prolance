package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.LeadRepository;
import com.dxc.crmservice.domain.model.aggregate.Lead;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.LeadEntity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa.LeadRepositoryJpa;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper.LeadPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class LeadRepositoryAdapter implements LeadRepository {

    private final LeadRepositoryJpa leadRepositoryJpa;
    private final LeadPersistenceMapper leadPersistenceMapper;

    @Override
    public Lead save(Lead lead) {
        LeadEntity entity = leadPersistenceMapper.toEntity(lead);
        return leadPersistenceMapper.toDomain(leadRepositoryJpa.save(entity));
    }

    @Override
    public Lead findById(UUID id) {
        return leadRepositoryJpa.findById(id)
                .map(leadPersistenceMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Lead update(Lead lead) {
        return save(lead);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        leadRepositoryJpa.findByIdAndTenantId(id, tenantId).ifPresent(leadRepositoryJpa::delete);
    }

    @Override
    public List<Lead> findAll(UUID tenantId) {
        return leadRepositoryJpa.findByTenantId(tenantId).stream()
                .map(leadPersistenceMapper::toDomain)
                .toList();
    }
}
