package com.dxc.billingservice.domain.model.event;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {
    UUID getEventId();
    UUID getTenantId();
    LocalDateTime getOccurredAt();
    String getEventType();
}
