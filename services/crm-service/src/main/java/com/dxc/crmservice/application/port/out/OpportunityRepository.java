package com.dxc.crmservice.application.port.out;

import com.dxc.crmservice.domain.model.aggregate.Opportunity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OpportunityRepository {
    Opportunity save(Opportunity opportunity);
    Opportunity findById(UUID id, UUID tenantId);
    Opportunity update(Opportunity opportunity);
    void delete(UUID id, UUID tenantId);
    Page<Opportunity> findAll(UUID tenantId, Pageable pageable);
}