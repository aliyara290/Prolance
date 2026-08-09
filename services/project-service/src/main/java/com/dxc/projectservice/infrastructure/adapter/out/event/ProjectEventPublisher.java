package com.dxc.projectservice.infrastructure.adapter.out.event;

import com.dxc.projectservice.application.port.out.ProjectRepository;
import com.dxc.projectservice.application.port.out.feign.UserFeignPort;
import com.dxc.projectservice.domain.model.aggregate.Project;
import com.dxc.projectservice.domain.model.event.member.MemberAdded;
import com.dxc.projectservice.domain.model.event.project.ProjectCreated;
import com.dxc.projectservice.infrastructure.adapter.out.event.dto.MemberAddedIntegrationEvent;
import com.dxc.projectservice.infrastructure.adapter.out.event.dto.ProjectCreatedIntegrationEvent;
import com.dxc.projectservice.infrastructure.adapter.out.feign.dto.ResponseWrapper;
import com.dxc.projectservice.infrastructure.adapter.out.feign.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProjectRepository projectRepository;
    private final UserFeignPort userFeignPort;
    private static final String TOPIC = "project-events";

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleMemberAddedEvent(MemberAdded event) {
        log.info("Publishing member added event to Kafka: projectId={}, userId={}", event.projectId(), event.userId());

        String projectName = projectRepository.findById(event.projectId(), event.tenantId())
                .map(Project::getName)
                .orElse("Unknown Project");

        Map<String, Object> payload = new HashMap<>();
        payload.put("tenantId", event.tenantId().toString());
        payload.put("projectId", event.projectId().toString());
        payload.put("userId", event.userId().toString());
        payload.put("role", event.role().name());
        payload.put("allocation", event.allocation());
        payload.put("actionBy", event.actionBy().toString());
        payload.put("projectName", projectName);

        try {
            ResponseWrapper<UserResponseDTO> memberResp = userFeignPort.getUser(event.userId());
            if (memberResp != null && memberResp.data() != null) {
                UserResponseDTO user = memberResp.data();
                payload.put("memberEmail", user.email());
                payload.put("memberName", user.firstName() + " " + user.lastName());
            }

            ResponseWrapper<UserResponseDTO> inviterResp = userFeignPort.getUser(event.actionBy());
            if (inviterResp != null && inviterResp.data() != null) {
                UserResponseDTO user = inviterResp.data();
                payload.put("inviterName", user.firstName() + " " + user.lastName());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch user details for member added event: {}", e.getMessage());
        }

        MemberAddedIntegrationEvent integrationEvent = MemberAddedIntegrationEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("MemberAddedIntegrationEvent")
                .tenantId(event.tenantId())
                .payload(payload)
                .build();

        kafkaTemplate.send(TOPIC, integrationEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleProjectCreatedEvent(ProjectCreated event) {
        if (event.projectManagerId() == null) {
            return;
        }

        log.info("Publishing project created event to Kafka: projectId={}, managerId={}", event.projectId(), event.projectManagerId());

        Map<String, Object> payload = new HashMap<>();
        payload.put("tenantId", event.tenantId().toString());
        payload.put("projectId", event.projectId().toString());
        payload.put("projectName", event.name());
        payload.put("projectManagerId", event.projectManagerId().toString());
        payload.put("actionBy", event.actionBy().toString());

        try {
            ResponseWrapper<UserResponseDTO> managerResp = userFeignPort.getUser(event.projectManagerId());
            if (managerResp != null && managerResp.data() != null) {
                UserResponseDTO user = managerResp.data();
                payload.put("managerEmail", user.email());
                payload.put("managerName", user.firstName() + " " + user.lastName());
            }

            ResponseWrapper<UserResponseDTO> creatorResp = userFeignPort.getUser(event.actionBy());
            if (creatorResp != null && creatorResp.data() != null) {
                UserResponseDTO user = creatorResp.data();
                payload.put("creatorName", user.firstName() + " " + user.lastName());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch user details for project created event: {}", e.getMessage());
        }

        ProjectCreatedIntegrationEvent integrationEvent = ProjectCreatedIntegrationEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("ProjectCreatedIntegrationEvent")
                .tenantId(event.tenantId())
                .payload(payload)
                .build();

        kafkaTemplate.send(TOPIC, integrationEvent);
    }
}
