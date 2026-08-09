package com.dxc.projectservice.infrastructure.adapter.out.event.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ProjectCreatedIntegrationEvent {
    private UUID eventId;
    private String eventType;
    private UUID tenantId;
    private Map<String, Object> payload;
}
