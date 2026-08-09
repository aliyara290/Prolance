package com.dxc.notificationservice.application.service.handler;

import com.dxc.notificationservice.infrastructure.adapter.in.kafka.dto.IntegrationEvent;

public interface NotificationEventHandler {
    boolean canHandle(String eventType);
    void handle(IntegrationEvent event);
}
