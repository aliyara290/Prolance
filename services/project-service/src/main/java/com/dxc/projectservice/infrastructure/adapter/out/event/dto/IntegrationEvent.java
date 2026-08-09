package com.dxc.projectservice.infrastructure.adapter.out.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntegrationEvent {
    private UUID eventId;
    private String eventType;
    private int version;
    private UUID tenantId;
    private LocalDateTime occurredOn;
}
