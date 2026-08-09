package com.dxc.projectservice.infrastructure.adapter.out.event;

import com.dxc.projectservice.infrastructure.adapter.out.event.dto.IntegrationEvent;
import com.dxc.projectservice.infrastructure.adapter.out.event.dto.MemberAddedIntegrationEvent;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.OutboxEventEntity;
import com.dxc.projectservice.infrastructure.adapter.out.persistence.jpa.OutboxEventRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxProcessor {

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${app.outbox.fixed-delay:5000}")
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEventEntity> pendingEvents = outboxRepository.findByStatusOrderByOccurredOnAsc("PENDING");
        if (pendingEvents.isEmpty()) {
            return;
        }
        
        log.info("Processing {} pending outbox events", pendingEvents.size());

        for (OutboxEventEntity entity : pendingEvents) {
            try {
                IntegrationEvent integrationEvent = mapToIntegrationEvent(entity);
                if (integrationEvent != null) {
                    kafkaTemplate.send("project-events", integrationEvent);
                    log.debug("Sent event {} to project-events topic", entity.getId());
                }
                
                entity.setStatus("PUBLISHED");
                entity.setPublishedAt(java.time.LocalDateTime.now());
                outboxRepository.save(entity);
            } catch (Exception e) {
                log.error("Failed to process outbox event {}", entity.getId(), e);
                entity.setRetryCount(entity.getRetryCount() + 1);
                entity.setErrorMessage(e.getMessage());
                if (entity.getRetryCount() >= 3) {
                    entity.setStatus("FAILED");
                }
                outboxRepository.save(entity);
            }
        }
    }

    private IntegrationEvent mapToIntegrationEvent(OutboxEventEntity entity) throws Exception {
        Map<String, Object> payloadMap = objectMapper.readValue(entity.getPayload(), new TypeReference<Map<String, Object>>() {});
        
        log.warn("Unmapped outbox event type: {}", entity.getType());
        return null;
    }
}
