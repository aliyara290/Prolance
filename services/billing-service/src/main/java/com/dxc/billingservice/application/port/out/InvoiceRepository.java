package com.dxc.billingservice.application.port.out;

import com.dxc.billingservice.domain.model.aggregate.Invoice;
import com.dxc.billingservice.domain.model.valueobject.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends GenericRepository<Invoice> {
    Page<Invoice> findByProjectId(UUID projectId, UUID tenantId, Pageable pageable);
    Page<Invoice> findByClientId(UUID clientId, UUID tenantId, Pageable pageable);
    Page<Invoice> findByStatus(InvoiceStatus status, UUID tenantId, Pageable pageable);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber, UUID tenantId);
    int countByTenantIdAndInvoiceNumberStartingWith(UUID tenantId, String prefix);
}
