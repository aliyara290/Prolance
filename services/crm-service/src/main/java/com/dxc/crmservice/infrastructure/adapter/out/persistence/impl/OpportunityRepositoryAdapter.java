package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.OpportunityRepository;
import com.dxc.crmservice.domain.model.aggregate.Opportunity;

import java.util.List;
import java.util.UUID;

public class OpportunityRepositoryAdapter implements OpportunityRepository {

    @Override
    public Opportunity save(Opportunity opportunity) {
        return null;
    }

    @Override
    public Opportunity findById(UUID id) {
        return null;
    }

    @Override
    public Opportunity update(Opportunity opportunity) {
        return null;
    }

    @Override
    public void delete(UUID id, UUID tenantId) {

    }

    @Override
    public List<Opportunity> findAll(UUID tenantId) {
        return List.of();
    }
}
