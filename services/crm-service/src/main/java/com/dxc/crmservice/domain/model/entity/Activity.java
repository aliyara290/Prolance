package com.dxc.crmservice.domain.model.entity;

import com.dxc.crmservice.domain.exception.BusinessRuleViolationException;
import com.dxc.crmservice.domain.exception.ValidationException;
import com.dxc.crmservice.domain.model.valueobject.ActivityType;
import com.dxc.crmservice.domain.model.valueobject.EntityType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Activity {

    private final UUID id;
    private final UUID tenantId;

    private final UUID entityId;
    private final EntityType entityType;

    private final ActivityType type;

    private String subject;
    private String description;

    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;

    private final UUID userId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Activity(UUID id,
                     UUID tenantId,
                     UUID entityId,
                     EntityType entityType,
                     ActivityType type,
                     String subject,
                     String description,
                     LocalDateTime scheduledAt,
                     UUID userId) {

        this.id = id == null ? UUID.randomUUID() : id;
        this.tenantId = requireNonNull(tenantId, "tenantId");

        this.entityId = requireNonNull(entityId, "entityId");
        this.entityType = requireNonNull(entityType, "entityType");

        this.type = requireNonNull(type, "type");
        this.userId = requireNonNull(userId, "userId");

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        this.completedAt = null;

        updateDetails(subject, description);

        this.scheduledAt = scheduledAt;
    }

    public static Activity create(UUID tenantId,
                                  UUID entityId,
                                  EntityType entityType,
                                  ActivityType type,
                                  String subject,
                                  String description,
                                  LocalDateTime scheduledAt,
                                  UUID userId) {

        return new Activity(
                null,
                tenantId,
                entityId,
                entityType,
                type,
                subject,
                description,
                scheduledAt,
                userId
        );
    }

    public static Activity rehydrate(UUID id,
                                     UUID tenantId,
                                     UUID entityId,
                                     EntityType entityType,
                                     ActivityType type,
                                     String subject,
                                     String description,
                                     LocalDateTime scheduledAt,
                                     LocalDateTime completedAt,
                                     UUID userId,
                                     LocalDateTime createdAt,
                                     LocalDateTime updatedAt) {

        Activity activity = new Activity(
                id,
                tenantId,
                entityId,
                entityType,
                type,
                subject,
                description,
                scheduledAt,
                userId
        );

        activity.completedAt = completedAt;
        activity.createdAt = requireNonNull(createdAt, "createdAt");
        activity.updatedAt = requireNonNull(updatedAt, "updatedAt");

        return activity;
    }

    // behavior

    public void complete() {
        if (this.completedAt != null) {
            throw new ValidationException("Activity already completed");
        }

        this.completedAt = LocalDateTime.now();
        touch();
    }

    public void reschedule(LocalDateTime newDate) {
        ensureNotCompleted();

        if (newDate == null || newDate.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Invalid scheduled date");
        }

        this.scheduledAt = newDate;
        touch();
    }

    public void updateDetails(String subject, String description) {
        ensureNotCompleted();

        this.subject = validateSubject(subject);
        this.description = description;

        touch();
    }

    // Rules

    private void ensureNotCompleted() {
        if (this.completedAt != null) {
            throw new BusinessRuleViolationException("Cannot modify a completed activity");
        }
    }

    private String validateSubject(String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            throw new ValidationException("Activity subject cannot be empty");
        }
        return subject.trim();
    }

    private static <T> T requireNonNull(T value, String field) {
        return Objects.requireNonNull(value, field + " cannot be null");
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

}