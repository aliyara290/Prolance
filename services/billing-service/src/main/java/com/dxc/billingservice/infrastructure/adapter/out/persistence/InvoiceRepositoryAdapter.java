package com.dxc.billingservice.infrastructure.adapter.out.persistence;

import com.dxc.billingservice.application.port.out.InvoiceRepository;
import com.dxc.billingservice.domain.model.aggregate.Invoice;
import com.dxc.billingservice.domain.model.valueobject.InvoiceStatus;
import com.dxc.billingservice.infrastructure.persistence.entity.InvoiceJpaEntity;
import com.dxc.billingservice.infrastructure.persistence.jpa.InvoiceJpaRepository;
import com.dxc.billingservice.infrastructure.persistence.mapper.PersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InvoiceRepositoryAdapter implements InvoiceRepository {

    private final InvoiceJpaRepository jpaRepository;
    private final PersistenceMapper mapper;

    @Override
    public Invoice save(Invoice invoice) {
        InvoiceJpaEntity entity = mapper.toInvoiceJpaEntity(invoice);
        InvoiceJpaEntity saved = jpaRepository.save(entity);
        return mapper.toInvoiceDomain(saved);
    }

    @Override
    public Optional<Invoice> findById(UUID id, UUID tenantId) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).map(entity -> mapper.toInvoiceDomain(entity));
    }

    @Override
    public void delete(UUID id, UUID tenantId) {
        jpaRepository.deleteByIdAndTenantId(id, tenantId);
    }

    @Override
    public Page<Invoice> findAll(UUID tenantId, Pageable pageable) {
        return jpaRepository.findByTenantId(tenantId, pageable).map(entity -> mapper.toInvoiceDomain(entity));
    }

    @Override
    public boolean existsById(UUID id, UUID tenantId) {
        return jpaRepository.existsByIdAndTenantId(id, tenantId);
    }

    @Override
    public Page<Invoice> findByProjectId(UUID projectId, UUID tenantId, Pageable pageable) {
        return jpaRepository.findByProjectIdAndTenantId(projectId, tenantId, pageable).map(entity -> mapper.toInvoiceDomain(entity));
    }

    @Override
    public Page<Invoice> findByClientId(UUID clientId, UUID tenantId, Pageable pageable) {
        return jpaRepository.findByClientIdAndTenantId(clientId, tenantId, pageable).map(entity -> mapper.toInvoiceDomain(entity));
    }

    @Override
    public Page<Invoice> findByStatus(InvoiceStatus status, UUID tenantId, Pageable pageable) {
        return jpaRepository.findByStatusAndTenantId(status, tenantId, pageable).map(entity -> mapper.toInvoiceDomain(entity));
    }

    @Override
    public Optional<Invoice> findByInvoiceNumber(String invoiceNumber, UUID tenantId) {
        return jpaRepository.findByInvoiceNumberAndTenantId(invoiceNumber, tenantId).map(entity -> mapper.toInvoiceDomain(entity));
    }

    @Override
    public int countByTenantIdAndInvoiceNumberStartingWith(UUID tenantId, String prefix) {
        return jpaRepository.countByTenantIdAndInvoiceNumberStartingWith(tenantId, prefix);
    }
}
