package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.OpportunityRepository;
import com.dxc.crmservice.domain.model.aggregate.Opportunity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.OpportunityEntity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa.OpportunityRepositoryJpa;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper.OpportunityPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class OpportunityRepositoryAdapter implements OpportunityRepository {

    private final OpportunityRepositoryJpa opportunityRepositoryJpa;
    private final OpportunityPersistenceMapper opportunityPersistenceMapper;

    @Override
    public Opportunity save(Opportunity opportunity) {
        OpportunityEntity entity = opportunityPersistenceMapper.toEntity(opportunity);
        return opportunityPersistenceMapper.toDomain(opportunityRepositoryJpa.save(entity));
    }

    @Override
    public Opportunity findById(UUID id) {
        return opportunityRepositoryJpa.findById(id)
                .map(opportunityPersistenceMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Opportunity update(Opportunity opportunity) {
        return save(opportunity);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        opportunityRepositoryJpa.findByIdAndTenantId(id, tenantId).ifPresent(opportunityRepositoryJpa::delete);
    }

    @Override
    public List<Opportunity> findAll(UUID tenantId) {
        return opportunityRepositoryJpa.findByTenantId(tenantId).stream()
                .map(opportunityPersistenceMapper::toDomain)
                .toList();
    }
}
