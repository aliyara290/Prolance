package com.dxc.taskservice.application.service;

import com.dxc.taskservice.application.port.out.TaskRepository;
import com.dxc.taskservice.domain.model.event.task.TaskCreated;
import com.dxc.taskservice.domain.model.event.task.TaskDeleted;
import com.dxc.taskservice.domain.model.event.task.TaskStatusChanged;
import com.dxc.taskservice.domain.model.event.task.TaskUpdated;
import com.dxc.taskservice.infrastructure.adapter.out.event.MilestoneProgressIntegrationEvent;
import com.dxc.taskservice.domain.model.aggregate.Task;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskProgressCalculator {

    private final TaskRepository taskRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "task-events";

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onTaskCreated(TaskCreated event) {
        Task task = taskRepository.findById(event.taskId(), event.tenantId()).orElse(null);
        if (task != null && task.getMilestoneId() != null) {
            calculateAndPublishProgress(task.getMilestoneId(), event.tenantId());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onTaskUpdated(TaskUpdated event) {
        Task task = taskRepository.findById(event.taskId(), event.tenantId()).orElse(null);
        if (task != null && task.getMilestoneId() != null) {
            calculateAndPublishProgress(task.getMilestoneId(), event.tenantId());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onTaskStatusChanged(TaskStatusChanged event) {
        Task task = taskRepository.findById(event.taskId(), event.tenantId()).orElse(null);
        if (task != null && task.getMilestoneId() != null) {
            calculateAndPublishProgress(task.getMilestoneId(), event.tenantId());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onTaskDeleted(TaskDeleted event) {
        if (event.milestoneId() != null) {
            calculateAndPublishProgress(event.milestoneId(), event.tenantId());
        }
    }

    private void calculateAndPublishProgress(UUID milestoneId, UUID tenantId) {
        log.debug("Calculating progress for milestone: {}", milestoneId);
        Page<Task> tasksPage = taskRepository.findByMilestoneId(milestoneId, tenantId, Pageable.unpaged());
        
        long totalTasks = tasksPage.getTotalElements();
        if (totalTasks == 0) {
            publishProgress(milestoneId, tenantId, 0f);
            return;
        }

        long completedTasks = tasksPage.getContent().stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .count();

        float progress = (float) completedTasks * 100 / totalTasks;
        progress = Math.round(progress * 10.0f) / 10.0f;
        
        publishProgress(milestoneId, tenantId, progress);
    }

    private void publishProgress(UUID milestoneId, UUID tenantId, float progress) {
        MilestoneProgressIntegrationEvent integrationEvent = MilestoneProgressIntegrationEvent.builder()
                .milestoneId(milestoneId)
                .tenantId(tenantId)
                .progressPercentage(progress)
                .build();
                
        log.info("Publishing milestone progress: milestoneId={}, progress={}", milestoneId, progress);
        kafkaTemplate.send(TOPIC, integrationEvent);
    }
}
