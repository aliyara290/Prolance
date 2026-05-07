package com.dxc.projectservice.domain.model.event;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {
    UUID eventId();
    LocalDateTime occurredOn();
    UUID tenantId();
    UUID projectId();
}
