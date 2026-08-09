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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskAssignedHandler implements NotificationEventHandler {

    private final NotificationTemplateRepository templateRepository;
    private final NotificationDeliveryService deliveryService;

    @Override
    public boolean canHandle(String eventType) {
        return "TaskAssignedIntegrationEvent".equals(eventType) || "TaskAssigned".equals(eventType);
    }

    @Override
    public void handle(IntegrationEvent event) {
        NotificationTemplate template = templateRepository.findByType(NotificationType.TASK_ASSIGNED)
                .orElseThrow(() -> new IllegalStateException("Template not found for TASK_ASSIGNED"));

        Map<String, Object> payload = event.getPayload();
        
        Object assigneeIdObj = payload.get("assigneeId");
        if (assigneeIdObj == null) {
            log.info("Task {} was unassigned, skipping notification", payload.get("taskId"));
            return;
        }

        UUID assigneeId = UUID.fromString((String) assigneeIdObj);
        UUID assignerId = UUID.fromString((String) payload.get("actionBy"));
        UUID taskId = UUID.fromString((String) payload.get("taskId"));
        
        // User details are now provided in the payload by the task-service
        String assignerName = (String) payload.getOrDefault("assignerName", "A user");
        String assigneeEmail = (String) payload.get("assigneeEmail");
        String recipientName = (String) payload.getOrDefault("assigneeName", "User");

        String taskTitle = (String) payload.getOrDefault("taskTitle", "a task");
        String projectId = (String) payload.getOrDefault("projectId", "");
        
        Map<String, String> variables = new HashMap<>();
        variables.put("assignerName", assignerName);
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
                recipientName // Recipient name
        );
    }
}
