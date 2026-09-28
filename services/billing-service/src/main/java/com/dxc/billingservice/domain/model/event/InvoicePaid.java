package com.dxc.billingservice.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class InvoicePaid implements DomainEvent {
    private final UUID eventId;
    private final UUID tenantId;
    private final UUID invoiceId;
    private final BigDecimal amountPaid;
    private final UUID paidBy;
    private final LocalDateTime occurredAt;

    public static InvoicePaid now(UUID tenantId, UUID invoiceId, BigDecimal amountPaid, UUID paidBy) {
        return new InvoicePaid(UUID.randomUUID(), tenantId, invoiceId, amountPaid, paidBy, LocalDateTime.now());
    }

    @Override
    public String getEventType() {
        return "INVOICE_PAID";
    }
}
