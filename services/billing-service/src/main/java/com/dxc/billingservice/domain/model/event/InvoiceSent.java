package com.dxc.billingservice.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class InvoiceSent implements DomainEvent {
    private final UUID eventId;
    private final UUID tenantId;
    private final UUID invoiceId;
    private final UUID clientId;
    private final BigDecimal totalAmount;
    private final UUID sentBy;
    private final LocalDateTime occurredAt;

    public static InvoiceSent now(UUID tenantId, UUID invoiceId, UUID clientId, BigDecimal totalAmount, UUID sentBy) {
        return new InvoiceSent(UUID.randomUUID(), tenantId, invoiceId, clientId, totalAmount, sentBy, LocalDateTime.now());
    }

    @Override
    public String getEventType() {
        return "INVOICE_SENT";
    }
}
