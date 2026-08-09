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
public class MemberAddedToProjectHandler implements NotificationEventHandler {

    private final NotificationTemplateRepository templateRepository;
    private final NotificationDeliveryService deliveryService;

    @Override
    public boolean canHandle(String eventType) {
        return "MemberAddedIntegrationEvent".equals(eventType) || "MemberAdded".equals(eventType);
    }

    @Override
    public void handle(IntegrationEvent event) {
        NotificationTemplate template = templateRepository.findByType(NotificationType.MEMBER_ADDED_TO_PROJECT)
                .orElseThrow(() -> new IllegalStateException("Template not found for MEMBER_ADDED_TO_PROJECT"));

        Map<String, Object> payload = event.getPayload();

        UUID projectId = UUID.fromString((String) payload.get("projectId"));
        UUID newMemberId = UUID.fromString((String) payload.get("userId"));
        UUID inviterId = UUID.fromString((String) payload.get("actionBy"));
        String role = (String) payload.get("role");

        // User details are provided in the enriched payload by the project-service
        String projectName = (String) payload.getOrDefault("projectName", "a project");
        String inviterName = (String) payload.getOrDefault("inviterName", "A user");
        String memberEmail = (String) payload.get("memberEmail");
        String memberName = (String) payload.getOrDefault("memberName", "User");

        Map<String, String> variables = new HashMap<>();
        variables.put("inviterName", inviterName);
        variables.put("projectName", projectName);
        variables.put("role", role);

        deliveryService.deliver(
                template,
                event.getTenantId(),
                newMemberId,
                inviterId,
                "Project",
                projectId,
                variables,
                memberEmail,
                memberName
        );
    }
}
