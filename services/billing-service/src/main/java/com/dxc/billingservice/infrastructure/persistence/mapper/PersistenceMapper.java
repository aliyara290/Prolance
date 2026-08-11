package com.dxc.billingservice.infrastructure.persistence.mapper;

import com.dxc.billingservice.domain.model.aggregate.Invoice;
import com.dxc.billingservice.domain.model.entity.BillRate;
import com.dxc.billingservice.domain.model.entity.InvoiceLineItem;
import com.dxc.billingservice.domain.model.entity.InvoiceStatusHistory;
import com.dxc.billingservice.domain.model.entity.TimeEntry;
import com.dxc.billingservice.infrastructure.persistence.entity.BillRateJpaEntity;
import com.dxc.billingservice.infrastructure.persistence.entity.InvoiceJpaEntity;
import com.dxc.billingservice.infrastructure.persistence.entity.InvoiceLineItemJpaEntity;
import com.dxc.billingservice.infrastructure.persistence.entity.InvoiceStatusHistoryJpaEntity;
import com.dxc.billingservice.infrastructure.persistence.entity.TimeEntryJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class PersistenceMapper {

    public TimeEntryJpaEntity toTimeEntryJpaEntity(TimeEntry timeEntry) {
        return TimeEntryJpaEntity.builder()
                .id(timeEntry.getId())
                .tenantId(timeEntry.getTenantId())
                .projectId(timeEntry.getProjectId())
                .userId(timeEntry.getUserId())
                .taskId(timeEntry.getTaskId())
                .startTime(timeEntry.getStartTime())
                .endTime(timeEntry.getEndTime())
                .durationMinutes(timeEntry.getDurationMinutes())
                .description(timeEntry.getDescription())
                .billable(timeEntry.isBillable())
                .createdBy(timeEntry.getCreatedBy())
                .createdAt(timeEntry.getCreatedAt())
                .updatedAt(timeEntry.getUpdatedAt())
                .build();
    }

    public TimeEntry toTimeEntryDomain(TimeEntryJpaEntity entity) {
        return TimeEntry.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .projectId(entity.getProjectId())
                .userId(entity.getUserId())
                .taskId(entity.getTaskId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .durationMinutes(entity.getDurationMinutes())
                .description(entity.getDescription())
                .billable(entity.isBillable())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public InvoiceJpaEntity toInvoiceJpaEntity(Invoice invoice) {
        InvoiceJpaEntity entity = InvoiceJpaEntity.builder()
                .id(invoice.getId())
                .tenantId(invoice.getTenantId())
                .projectId(invoice.getProjectId())
                .clientId(invoice.getClientId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .status(invoice.getStatus())
                .billingType(invoice.getBillingType())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .periodStartDate(invoice.getPeriodStartDate())
                .periodEndDate(invoice.getPeriodEndDate())
                .subtotal(invoice.getSubtotal())
                .taxRate(invoice.getTaxRate())
                .taxAmount(invoice.getTaxAmount())
                .totalAmount(invoice.getTotalAmount())
                .currency(invoice.getCurrency())
                .notes(invoice.getNotes())
                .attachmentId(invoice.getAttachmentId())
                .createdBy(invoice.getCreatedBy())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .lineItems(new ArrayList<>())
                .statusHistory(new ArrayList<>())
                .build();

        if (invoice.getLineItems() != null) {
            invoice.getLineItems().forEach(li -> {
                InvoiceLineItemJpaEntity liEntity = toInvoiceLineItemJpaEntity(li, entity);
                entity.getLineItems().add(liEntity);
            });
        }

        if (invoice.getStatusHistory() != null) {
            invoice.getStatusHistory().forEach(sh -> {
                InvoiceStatusHistoryJpaEntity shEntity = toInvoiceStatusHistoryJpaEntity(sh, entity);
                entity.getStatusHistory().add(shEntity);
            });
        }

        return entity;
    }

    public Invoice toInvoiceDomain(InvoiceJpaEntity entity) {
        return Invoice.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .projectId(entity.getProjectId())
                .clientId(entity.getClientId())
                .invoiceNumber(entity.getInvoiceNumber())
                .status(entity.getStatus())
                .billingType(entity.getBillingType())
                .issueDate(entity.getIssueDate())
                .dueDate(entity.getDueDate())
                .periodStartDate(entity.getPeriodStartDate())
                .periodEndDate(entity.getPeriodEndDate())
                .subtotal(entity.getSubtotal())
                .taxRate(entity.getTaxRate())
                .taxAmount(entity.getTaxAmount())
                .totalAmount(entity.getTotalAmount())
                .currency(entity.getCurrency())
                .notes(entity.getNotes())
                .attachmentId(entity.getAttachmentId())
                .lineItems(entity.getLineItems() != null
                        ? entity.getLineItems().stream().map(this::toInvoiceLineItemDomain).collect(Collectors.toCollection(ArrayList::new))
                        : new ArrayList<>())
                .statusHistory(entity.getStatusHistory() != null
                        ? entity.getStatusHistory().stream().map(this::toInvoiceStatusHistoryDomain).collect(Collectors.toCollection(ArrayList::new))
                        : new ArrayList<>())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private InvoiceLineItemJpaEntity toInvoiceLineItemJpaEntity(InvoiceLineItem li, InvoiceJpaEntity invoice) {
        return InvoiceLineItemJpaEntity.builder()
                .id(li.getId())
                .tenantId(li.getTenantId())
                .invoice(invoice)
                .userId(li.getUserId())
                .description(li.getDescription())
                .quantity(li.getQuantity())
                .unit(li.getUnit())
                .unitPrice(li.getUnitPrice())
                .lineTotal(li.getLineTotal())
                .displayOrder(li.getDisplayOrder())
                .createdAt(li.getCreatedAt())
                .updatedAt(li.getUpdatedAt())
                .build();
    }

    private InvoiceLineItem toInvoiceLineItemDomain(InvoiceLineItemJpaEntity entity) {
        return InvoiceLineItem.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .invoiceId(entity.getInvoice().getId())
                .userId(entity.getUserId())
                .description(entity.getDescription())
                .quantity(entity.getQuantity())
                .unit(entity.getUnit())
                .unitPrice(entity.getUnitPrice())
                .lineTotal(entity.getLineTotal())
                .displayOrder(entity.getDisplayOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private InvoiceStatusHistoryJpaEntity toInvoiceStatusHistoryJpaEntity(InvoiceStatusHistory sh, InvoiceJpaEntity invoice) {
        return InvoiceStatusHistoryJpaEntity.builder()
                .id(sh.getId())
                .tenantId(sh.getTenantId())
                .invoice(invoice)
                .previousStatus(sh.getPreviousStatus())
                .newStatus(sh.getNewStatus())
                .comment(sh.getComment())
                .changedBy(sh.getChangedBy())
                .changedAt(sh.getChangedAt())
                .build();
    }

    private InvoiceStatusHistory toInvoiceStatusHistoryDomain(InvoiceStatusHistoryJpaEntity entity) {
        return InvoiceStatusHistory.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .invoiceId(entity.getInvoice().getId())
                .previousStatus(entity.getPreviousStatus())
                .newStatus(entity.getNewStatus())
                .comment(entity.getComment())
                .changedBy(entity.getChangedBy())
                .changedAt(entity.getChangedAt())
                .build();
    }

    public BillRateJpaEntity toBillRateJpaEntity(BillRate rate) {
        return BillRateJpaEntity.builder()
                .id(rate.getId())
                .tenantId(rate.getTenantId())
                .projectId(rate.getProjectId())
                .userId(rate.getUserId())
                .seniorityLevel(rate.getSeniorityLevel())
                .educationLevel(rate.getEducationLevel())
                .hourlyRate(rate.getHourlyRate())
                .dailyRate(rate.getDailyRate())
                .currency(rate.getCurrency())
                .effectiveFrom(rate.getEffectiveFrom())
                .effectiveTo(rate.getEffectiveTo())
                .createdBy(rate.getCreatedBy())
                .createdAt(rate.getCreatedAt())
                .updatedAt(rate.getUpdatedAt())
                .build();
    }

    public BillRate toBillRateDomain(BillRateJpaEntity entity) {
        return BillRate.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .projectId(entity.getProjectId())
                .userId(entity.getUserId())
                .seniorityLevel(entity.getSeniorityLevel())
                .educationLevel(entity.getEducationLevel())
                .hourlyRate(entity.getHourlyRate())
                .dailyRate(entity.getDailyRate())
                .currency(entity.getCurrency())
                .effectiveFrom(entity.getEffectiveFrom())
                .effectiveTo(entity.getEffectiveTo())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
