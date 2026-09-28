package com.dxc.billingservice.application.port.out;

import com.dxc.billingservice.domain.model.entity.BillRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillRateRepository extends GenericRepository<BillRate> {
    List<BillRate> findByProjectId(UUID projectId, UUID tenantId);
    Optional<BillRate> findByProjectIdAndUserId(UUID projectId, UUID userId, UUID tenantId);
    List<BillRate> findActiveByProjectId(UUID projectId, UUID tenantId, LocalDate date);
}
