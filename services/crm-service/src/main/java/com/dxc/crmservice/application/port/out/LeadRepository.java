package com.dxc.crmservice.application.port.out;

import com.dxc.crmservice.domain.model.aggregate.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LeadRepository {
    Lead save(Lead lead);
    Lead findById(UUID id, UUID tenantId);
    Lead update(Lead lead);
    void delete(UUID id, UUID tenantId);
    Page<Lead> findAll(UUID tenantId, Pageable pageable);
}
