package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.OpportunityRepository;
import com.dxc.crmservice.domain.model.aggregate.Opportunity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.entity.OpportunityEntity;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.jpa.OpportunityRepositoryJpa;
import com.dxc.crmservice.infrastructure.adapter.out.persistence.mapper.OpportunityPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class OpportunityRepositoryAdapter implements OpportunityRepository {

    private final OpportunityRepositoryJpa opportunityRepositoryJpa;
    private final OpportunityPersistenceMapper opportunityPersistenceMapper;

    @Override
    public Opportunity save(Opportunity domain) {
        OpportunityEntity entity = opportunityPersistenceMapper.toEntity(domain);
        OpportunityEntity saved = opportunityRepositoryJpa.save(entity);
        return opportunityPersistenceMapper.toDomain(saved);
    }

    @Override
    public Opportunity findById(UUID id, UUID tenantId) {
        return opportunityRepositoryJpa.findByIdAndTenantId(id, tenantId)
                .map(opportunityPersistenceMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Opportunity update(Opportunity domain) {
        if (!opportunityRepositoryJpa.existsById(domain.getId())) {
            throw new IllegalArgumentException("Opportunity with ID " + domain.getId() + " does not exist");
        }
        OpportunityEntity entity = opportunityPersistenceMapper.toEntity(domain);
        OpportunityEntity updated = opportunityRepositoryJpa.save(entity);
        return opportunityPersistenceMapper.toDomain(updated);
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        if (opportunityRepositoryJpa.existsByIdAndTenantId(id, tenantId)) {
            opportunityRepositoryJpa.deleteById(id);
        } else {
            throw new IllegalArgumentException("Opportunity with ID " + id + " does not exist for the given tenant");
        }
    }

    @Override
    public Page<Opportunity> findAll(UUID tenantId, Pageable pageable) {
        return opportunityRepositoryJpa.findAllByTenantId(tenantId, pageable)
                .map(opportunityPersistenceMapper::toDomain);
    }
}
