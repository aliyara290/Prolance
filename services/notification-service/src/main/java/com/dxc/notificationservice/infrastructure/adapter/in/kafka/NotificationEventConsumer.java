package com.dxc.notificationservice.infrastructure.adapter.in.kafka;

import com.dxc.notificationservice.application.port.out.ProcessedEventRepository;
import com.dxc.notificationservice.application.service.handler.NotificationEventHandler;
import com.dxc.notificationservice.infrastructure.adapter.in.kafka.dto.IntegrationEvent;
import com.dxc.notificationservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final List<NotificationEventHandler> handlers;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(
            topics = {"tenant-events", "project-events", "task-events", "crm-events"},
            groupId = "notification-service-group"
    )
    public void consumeEvent(IntegrationEvent event) {
        log.info("Received event: {} with ID: {}", event.getEventType(), event.getEventId());

        if (event.getEventId() == null || event.getEventType() == null) {
            log.warn("Skipping invalid event (missing ID or Type)");
            return;
        }

        // Idempotency check
        if (processedEventRepository.existsById(event.getEventId())) {
            log.debug("Skipping already processed event: {}", event.getEventId());
            return;
        }

        try {
            // Set context
            if (event.getTenantId() != null) {
                TenantContextHolder.setTenantId(event.getTenantId().toString());
            }

            // Find and execute handler
            boolean handled = false;
            for (NotificationEventHandler handler : handlers) {
                if (handler.canHandle(event.getEventType())) {
                    handler.handle(event);
                    handled = true;
                    break;
                }
            }

            if (!handled) {
                log.debug("No handler found for event type: {}", event.getEventType());
            }

            // Mark as processed
            processedEventRepository.markAsProcessed(event.getEventId(), event.getEventType());

        } catch (Exception e) {
            log.error("Error processing event: {}", event.getEventId(), e);
            throw e; // Will trigger Kafka retry
        } finally {
            TenantContextHolder.clear();
        }
    }
}
