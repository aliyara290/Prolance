package com.dxc.crmservice.application.port.out;

import com.dxc.crmservice.domain.model.aggregate.Opportunity;

import java.util.List;
import java.util.UUID;

public interface OpportunityRepository {
    Opportunity save(Opportunity opportunity);
    Opportunity findById(UUID id);
    Opportunity update(Opportunity opportunity);
    void delete(UUID id, UUID tenantId);
    List<Opportunity> findAll(UUID tenantId);
}