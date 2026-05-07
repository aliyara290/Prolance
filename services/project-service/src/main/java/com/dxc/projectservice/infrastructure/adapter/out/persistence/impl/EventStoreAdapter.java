package com.dxc.projectservice.infrastructure.adapter.out.persistence.impl;

import com.dxc.projectservice.application.port.out.EventStorePort;
import com.dxc.projectservice.domain.model.event.DomainEvent;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.OutboxEventEntity;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.jpa.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventStoreAdapter implements EventStorePort {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void save(DomainEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            
            OutboxEventEntity entity = OutboxEventEntity.builder()
                    .id(event.eventId())
                    .aggregateId(event.projectId())
                    .aggregateType("Project")
                    .type(event.getClass().getSimpleName())
                    .payload(payload)
                    .occurredOn(event.occurredOn())
                    .build();

            outboxEventRepository.save(entity);
            log.debug("Saved domain event to outbox: {}", event.eventId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize domain event: {}", event.eventId(), e);
            throw new RuntimeException("Failed to serialize domain event", e);
        }
    }
}
