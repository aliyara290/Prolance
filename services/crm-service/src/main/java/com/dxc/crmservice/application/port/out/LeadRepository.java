package com.dxc.crmservice.application.port.out;

import com.dxc.crmservice.domain.model.aggregate.Lead;

import java.util.List;
import java.util.UUID;

public interface LeadRepository {
    Lead save(Lead lead);
    Lead findById(UUID id);
    Lead update(Lead lead);
    void delete(UUID id, UUID tenantId);
    List<Lead> findAll(UUID tenantId);
}