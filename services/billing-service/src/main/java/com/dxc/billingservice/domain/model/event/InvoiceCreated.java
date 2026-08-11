package com.dxc.billingservice.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class InvoiceCreated implements DomainEvent {
    private final UUID eventId;
    private final UUID tenantId;
    private final UUID invoiceId;
    private final UUID projectId;
    private final UUID clientId;
    private final String invoiceNumber;
    private final UUID createdBy;
    private final LocalDateTime occurredAt;

    public static InvoiceCreated now(UUID tenantId, UUID invoiceId, UUID projectId, UUID clientId, String invoiceNumber, UUID createdBy) {
        return new InvoiceCreated(UUID.randomUUID(), tenantId, invoiceId, projectId, clientId, invoiceNumber, createdBy, LocalDateTime.now());
    }

    @Override
    public String getEventType() {
        return "INVOICE_CREATED";
    }
}
