package com.dxc.crmservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.crmservice.application.port.out.LeadRepository;
import com.dxc.crmservice.domain.model.aggregate.Lead;

import java.util.List;
import java.util.UUID;

public class LeadRepositoryAdapter implements LeadRepository {


    @Override
    public Lead save(Lead lead) {
        return null;
    }

    @Override
    public Lead findById(UUID id) {
        return null;
    }

    @Override
    public Lead update(Lead lead) {
        return null;
    }

    @Override
    public void delete(UUID id, UUID tenantId) {

    }

    @Override
    public List<Lead> findAll(UUID tenantId) {
        return List.of();
    }
}
