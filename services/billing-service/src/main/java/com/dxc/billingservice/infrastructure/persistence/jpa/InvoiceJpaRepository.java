package com.dxc.billingservice.infrastructure.persistence.jpa;

import com.dxc.billingservice.domain.model.valueobject.InvoiceStatus;
import com.dxc.billingservice.infrastructure.persistence.entity.InvoiceJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceJpaRepository extends JpaRepository<InvoiceJpaEntity, UUID> {
    Page<InvoiceJpaEntity> findByTenantId(UUID tenantId, Pageable pageable);
    Page<InvoiceJpaEntity> findByProjectIdAndTenantId(UUID projectId, UUID tenantId, Pageable pageable);
    Page<InvoiceJpaEntity> findByClientIdAndTenantId(UUID clientId, UUID tenantId, Pageable pageable);
    Page<InvoiceJpaEntity> findByStatusAndTenantId(InvoiceStatus status, UUID tenantId, Pageable pageable);
    Optional<InvoiceJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);
    Optional<InvoiceJpaEntity> findByInvoiceNumberAndTenantId(String invoiceNumber, UUID tenantId);
    boolean existsByIdAndTenantId(UUID id, UUID tenantId);
    int countByTenantIdAndInvoiceNumberStartingWith(UUID tenantId, String prefix);
    void deleteByIdAndTenantId(UUID id, UUID tenantId);
}
