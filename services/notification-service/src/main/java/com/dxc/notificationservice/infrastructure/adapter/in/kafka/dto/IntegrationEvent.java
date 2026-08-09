package com.dxc.notificationservice.infrastructure.adapter.in.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntegrationEvent {
    private UUID eventId;
    private String eventType;
    private int version;
    private UUID tenantId;
    private LocalDateTime occurredOn;
    private Map<String, Object> payload;
}
