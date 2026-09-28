package com.dxc.billingservice.domain.model.entity;

import com.dxc.billingservice.domain.model.valueobject.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class InvoiceStatusHistory {

    private final UUID id;
    private final UUID tenantId;
    private final UUID invoiceId;
    private final InvoiceStatus previousStatus;
    private final InvoiceStatus newStatus;
    private final String comment;
    private final UUID changedBy;
    private final LocalDateTime changedAt;

    public static InvoiceStatusHistory create(
            UUID tenantId,
            UUID invoiceId,
            InvoiceStatus previousStatus,
            InvoiceStatus newStatus,
            UUID changedBy,
            String comment
    ) {
        return InvoiceStatusHistory.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .invoiceId(invoiceId)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .comment(comment)
                .changedAt(LocalDateTime.now())
                .build();
    }
}
