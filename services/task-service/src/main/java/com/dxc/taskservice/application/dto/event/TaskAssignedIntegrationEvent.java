package com.dxc.taskservice.application.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignedIntegrationEvent {
    private UUID eventId;
    private String eventType;
    private UUID tenantId;
    private Map<String, Object> payload;
}
