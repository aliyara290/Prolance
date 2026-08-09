package com.dxc.notificationservice.application.service.handler;

import com.dxc.notificationservice.application.port.out.NotificationTemplateRepository;
import com.dxc.notificationservice.application.service.NotificationDeliveryService;
import com.dxc.notificationservice.domain.model.entity.NotificationTemplate;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.infrastructure.adapter.in.kafka.dto.IntegrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskCommentAddedHandler implements NotificationEventHandler {

    private final NotificationTemplateRepository templateRepository;
    private final NotificationDeliveryService deliveryService;

    @Override
    public boolean canHandle(String eventType) {
        return "TaskCommentAddedIntegrationEvent".equals(eventType);
    }

    @Override
    public void handle(IntegrationEvent event) {
        NotificationTemplate template = templateRepository.findByType(NotificationType.COMMENT_ADDED)
                .orElseThrow(() -> new IllegalStateException("Template not found for COMMENT_ADDED"));

        Map<String, Object> payload = event.getPayload();
        
        Object assigneeIdObj = payload.get("assigneeId");
        if (assigneeIdObj == null) {
            log.info("Task {} is unassigned, skipping comment notification", payload.get("taskId"));
            return;
        }
        
        UUID assigneeId = UUID.fromString((String) assigneeIdObj);
        UUID assignerId = UUID.fromString((String) payload.get("actionBy"));
        UUID taskId = UUID.fromString((String) payload.get("taskId"));
        
        String commenterName = (String) payload.getOrDefault("commenterName", "A user");
        String assigneeEmail = (String) payload.get("assigneeEmail");
        String recipientName = (String) payload.getOrDefault("assigneeName", "User");
        String taskTitle = (String) payload.getOrDefault("taskTitle", "a task");
        String projectId = (String) payload.getOrDefault("projectId", "");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("commenterName", commenterName);
        variables.put("taskTitle", taskTitle);
        variables.put("taskId", taskId.toString());
        variables.put("projectId", projectId);

        deliveryService.deliver(
                template,
                event.getTenantId(),
                assigneeId,
                assignerId,
                "Task",
                taskId,
                variables,
                assigneeEmail,
                recipientName
        );
    }
}
