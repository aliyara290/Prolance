package com.dxc.projectservice.infrastructure.adapter.in.event;

import com.dxc.projectservice.domain.model.event.DomainEvent;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.AuditLogEntity;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.jpa.AuditLogRepositoryJpa;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogListener {

    private final AuditLogRepositoryJpa auditLogRepository;
    private final ObjectMapper objectMapper;

    @EventListener
    public void handleDomainEvent(DomainEvent event) {
        log.debug("Received domain event for audit logging: {}", event.eventId());
        
        try {
            String payload = objectMapper.writeValueAsString(event);
            
            AuditLogEntity auditLog = AuditLogEntity.builder()
                    .id(UUID.randomUUID())
                    .tenantId(event.tenantId())
                    .aggregateId(event.projectId())
                    .actionType(event.getClass().getSimpleName())
                    .actionBy(event.actionBy())
                    .payload(payload)
                    .occurredOn(event.occurredOn())
                    .build();
                    
            auditLogRepository.save(auditLog);
            log.debug("Successfully saved audit log for event: {}", event.eventId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event payload for audit log. EventId: {}", event.eventId(), e);
        } catch (Exception e) {
            log.error("Failed to save audit log for event: {}", event.eventId(), e);
        }
    }
}
