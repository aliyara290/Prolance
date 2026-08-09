package com.dxc.projectservice.infrastructure.adapter.in.event;

import com.dxc.projectservice.application.service.MilestoneService;
import com.dxc.projectservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventKafkaListener {

    private final MilestoneService milestoneService;

    @KafkaListener(topics = "task-events", groupId = "project-service-group")
    public void handleTaskEvent(MilestoneProgressIntegrationEvent event) {
        log.info("Received MilestoneProgressIntegrationEvent for milestone: {}, progress: {}", event.getMilestoneId(), event.getProgressPercentage());
        
        try {
            TenantContextHolder.setTenantId(event.getTenantId().toString());
            
            milestoneService.updateProgressFromTask(event.getMilestoneId(), event.getProgressPercentage());
            
        } catch (Exception e) {
            log.error("Error processing MilestoneProgressIntegrationEvent", e);
        } finally {
            TenantContextHolder.clear();
        }
    }
}
