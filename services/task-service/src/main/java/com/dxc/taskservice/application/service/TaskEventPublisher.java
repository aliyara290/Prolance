package com.dxc.taskservice.application.service;

import com.dxc.taskservice.application.dto.event.TaskAssignedIntegrationEvent;
import com.dxc.taskservice.domain.model.event.assignment.UserAssigned;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.dxc.taskservice.application.port.out.TaskRepository;
import com.dxc.taskservice.application.port.out.feign.UserFeignPort;
import com.dxc.taskservice.domain.model.aggregate.Task;
import com.dxc.taskservice.infrastructure.adapter.out.feign.dto.ResponseWrapper;
import com.dxc.taskservice.infrastructure.adapter.out.feign.dto.UserResponseDTO;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TaskRepository taskRepository;
    private final UserFeignPort userFeignPort;
    private static final String TOPIC = "task-events";

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleUserAssignedEvent(UserAssigned event) {
        log.info("Publishing task assigned event to Kafka: taskId={}, userId={}", event.taskId(), event.userId());
        
        String taskTitle = "Unknown Task";
        String projectId = "";
        Task task = taskRepository.findById(event.taskId(), event.tenantId()).orElse(null);
        if (task != null) {
            taskTitle = task.getTitle();
            projectId = task.getProjectId().toString();
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("taskId", event.taskId().toString());
        payload.put("taskTitle", taskTitle);
        payload.put("projectId", projectId);
        payload.put("assigneeId", event.userId().toString());
        payload.put("actionBy", event.actionBy().toString());
        
        try {
            ResponseWrapper<UserResponseDTO> assigneeResp = userFeignPort.getUser(event.userId());
            if (assigneeResp != null && assigneeResp.data() != null) {
                UserResponseDTO user = assigneeResp.data();
                payload.put("assigneeEmail", user.email());
                payload.put("assigneeName", user.firstName() + " " + user.lastName());
            }

            ResponseWrapper<UserResponseDTO> assignerResp = userFeignPort.getUser(event.actionBy());
            if (assignerResp != null && assignerResp.data() != null) {
                UserResponseDTO user = assignerResp.data();
                payload.put("assignerName", user.firstName() + " " + user.lastName());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch user details for task assignment event: {}", e.getMessage());
        }

        TaskAssignedIntegrationEvent integrationEvent = TaskAssignedIntegrationEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("TaskAssignedIntegrationEvent")
                .tenantId(event.tenantId())
                .payload(payload)
                .build();
                
        kafkaTemplate.send(TOPIC, integrationEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleCommentAddedEvent(com.dxc.taskservice.domain.model.event.comment.CommentAdded event) {
        log.info("Publishing comment added event to Kafka: taskId={}, commentId={}", event.taskId(), event.commentId());

        Task task = taskRepository.findById(event.taskId(), event.tenantId()).orElse(null);
        if (task == null || task.getAssignments() == null) return;

        String taskTitle = task.getTitle();
        String projectId = task.getProjectId().toString();

        // Fetch commenter details
        String commenterName = "A user";
        try {
            ResponseWrapper<UserResponseDTO> commenterResp = userFeignPort.getUser(event.actionBy());
            if (commenterResp != null && commenterResp.data() != null) {
                UserResponseDTO user = commenterResp.data();
                commenterName = user.firstName() + " " + user.lastName();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch user details for comment added event: {}", e.getMessage());
        }

        for (com.dxc.taskservice.domain.model.entity.TaskAssignment assignment : task.getAssignments()) {
            if (assignment.getStatus() == com.dxc.taskservice.domain.model.valueobject.TaskAssignmentStatus.ACTIVE 
                && !assignment.getUserId().equals(event.actionBy())) {
                
                Map<String, Object> payload = new HashMap<>();
                payload.put("taskId", event.taskId().toString());
                payload.put("taskTitle", taskTitle);
                payload.put("projectId", projectId);
                payload.put("commentId", event.commentId().toString());
                payload.put("actionBy", event.actionBy().toString());
                payload.put("commenterName", commenterName);
                
                // Add recipient details
                payload.put("assigneeId", assignment.getUserId().toString());
                try {
                    ResponseWrapper<UserResponseDTO> assigneeResp = userFeignPort.getUser(assignment.getUserId());
                    if (assigneeResp != null && assigneeResp.data() != null) {
                        UserResponseDTO user = assigneeResp.data();
                        payload.put("assigneeEmail", user.email());
                        payload.put("assigneeName", user.firstName() + " " + user.lastName());
                    }
                } catch (Exception e) {
                    log.warn("Failed to fetch recipient details: {}", e.getMessage());
                }

                com.dxc.taskservice.application.dto.event.TaskCommentAddedIntegrationEvent integrationEvent = 
                    com.dxc.taskservice.application.dto.event.TaskCommentAddedIntegrationEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType("TaskCommentAddedIntegrationEvent")
                        .tenantId(event.tenantId())
                        .payload(payload)
                        .build();

                kafkaTemplate.send(TOPIC, integrationEvent);
            }
        }
    }
}

