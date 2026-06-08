package com.dxc.projectservice.domain.model.entity;

import com.dxc.projectservice.domain.exception.ValidationException;
import com.dxc.projectservice.domain.model.valueobject.MilestoneStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Milestone {
    private final UUID id;
    private final UUID tenantId;
    private UUID projectId;
    private String title;
    private String description;
    private MilestoneStatus status;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    private int sequenceOrder;
    private float progressPercentage;
    private final LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;

    public static Milestone create(UUID tenantId, String title, String description, LocalDateTime startDate, LocalDateTime dueDate, int sequenceOrder, UUID createdBy) {
        validateDates(startDate, dueDate);
        return Milestone.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .title(title)
                .description(description)
                .status(MilestoneStatus.PENDING)
                .startDate(startDate)
                .dueDate(dueDate)
                .sequenceOrder(sequenceOrder)
                .progressPercentage(0)
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
    }

    public void updateProgress(float progress) {
        if (progress < 0 || progress > 100) {
            throw new ValidationException("Progress percentage must be between 0 and 100");
        }
        this.progressPercentage = progress;

        touch();

        if (progress >= 100 && this.status != MilestoneStatus.COMPLETED) {
            complete();
        } else if (progress > 0 && this.status == MilestoneStatus.PENDING) {
            this.status = MilestoneStatus.IN_PROGRESS;
        }
    }

    public void update(String title, String description, LocalDateTime start, LocalDateTime due) {
        validateDates(start, due);
        this.title = title;
        this.description = description;
        this.startDate = start;
        this.dueDate = due;
        touch();
    }

    public void complete() {
        this.status = MilestoneStatus.COMPLETED;
        this.progressPercentage = 100;
        this.completedAt = LocalDateTime.now();

        touch();
    }

    private static void validateDates(LocalDateTime start, LocalDateTime due) {
        if (start == null) throw new ValidationException("Start date is required");
        if (due != null && due.isBefore(start)) {
            throw new ValidationException("Due date cannot be before start date");
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}