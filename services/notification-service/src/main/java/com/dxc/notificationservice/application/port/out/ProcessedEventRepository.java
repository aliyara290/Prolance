package com.dxc.notificationservice.application.port.out;

import java.util.UUID;

public interface ProcessedEventRepository {
    boolean existsById(UUID eventId);
    void markAsProcessed(UUID eventId, String eventType);
}
