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
public class ProjectManagerAssignedHandler implements NotificationEventHandler {

    private final NotificationTemplateRepository templateRepository;
    private final NotificationDeliveryService deliveryService;

    @Override
    public boolean canHandle(String eventType) {
        return "ProjectCreatedIntegrationEvent".equals(eventType);
    }

    @Override
    public void handle(IntegrationEvent event) {
        log.info("Processing ProjectManagerAssigned from ProjectCreatedIntegrationEvent: {}", event.getEventId());

        NotificationTemplate template = templateRepository.findByType(NotificationType.PROJECT_MANAGER_ASSIGNED)
                .orElseThrow(() -> new IllegalStateException("Template not found for PROJECT_MANAGER_ASSIGNED"));

        Map<String, Object> payload = event.getPayload();
        UUID tenantId = event.getTenantId();
        Object pmIdObj = payload.get("projectManagerId");
        if (pmIdObj == null) {
            log.info("No project manager assigned for project {}, skipping notification", payload.get("projectId"));
            return;
        }
        
        UUID projectManagerId = UUID.fromString((String) pmIdObj);
        UUID actionBy = UUID.fromString((String) payload.get("actionBy"));
        UUID projectId = UUID.fromString((String) payload.get("projectId"));
        
        if (projectManagerId.equals(actionBy)) {
            log.info("Project creator is the manager, skipping notification");
            return;
        }

        String assignerName = (String) payload.getOrDefault("creatorName", "A user");
        String assigneeEmail = (String) payload.get("managerEmail");
        String recipientName = (String) payload.getOrDefault("managerName", "User");
        String projectName = (String) payload.getOrDefault("projectName", "a project");

        Map<String, String> variables = new HashMap<>();
        variables.put("creatorName", assignerName);
        variables.put("projectName", projectName);
        variables.put("projectId", projectId.toString());
        variables.put("managerName", recipientName);
        variables.put("managerEmail", assigneeEmail);

        deliveryService.deliver(
                template,
                tenantId,
                projectManagerId,
                actionBy,
                "Project",
                projectId,
                variables,
                assigneeEmail,
                recipientName
        );
    }
}
