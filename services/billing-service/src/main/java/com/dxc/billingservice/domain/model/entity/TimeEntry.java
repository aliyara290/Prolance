package com.dxc.billingservice.domain.model.entity;

import com.dxc.billingservice.domain.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TimeEntry {
    private final UUID id;
    private final UUID tenantId;
    private final UUID projectId;
    private final UUID userId;
    private final UUID taskId; // Optional
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMinutes;
    
    private String description;
    private boolean billable;
    
    private final UUID createdBy;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TimeEntry create(
            UUID tenantId,
            UUID projectId,
            UUID userId,
            UUID taskId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String description,
            boolean billable,
            UUID createdBy
    ) {
        validateRequired(tenantId, "Tenant ID is required");
        validateRequired(projectId, "Project ID is required");
        validateRequired(userId, "User ID is required");
        validateRequired(startTime, "Start time is required");
        validateRequired(endTime, "End time is required");
        
        if (endTime.isBefore(startTime)) {
            throw new BusinessRuleException("End time cannot be before start time");
        }
        
        long duration = Duration.between(startTime, endTime).toMinutes();
        if (duration <= 0) {
            throw new BusinessRuleException("Duration must be greater than zero");
        }

        return TimeEntry.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .projectId(projectId)
                .userId(userId)
                .taskId(taskId)
                .startTime(startTime)
                .endTime(endTime)
                .durationMinutes(duration)
                .description(description)
                .billable(billable)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void update(LocalDateTime startTime, LocalDateTime endTime, String description, boolean billable) {
        validateRequired(startTime, "Start time is required");
        validateRequired(endTime, "End time is required");
        
        if (endTime.isBefore(startTime)) {
            throw new BusinessRuleException("End time cannot be before start time");
        }
        
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = Duration.between(startTime, endTime).toMinutes();
        this.description = description;
        this.billable = billable;
        this.updatedAt = LocalDateTime.now();
    }

    private static void validateRequired(Object value, String message) {
        if (value == null) {
            throw new BusinessRuleException(message);
        }
    }
}
