package com.dxc.crmservice.application.dto.activity.req;

import com.dxc.crmservice.domain.model.valueobject.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateActivityRequest(
    @NotNull(message = "Activity type is required")
    ActivityType type,

    @NotBlank(message = "Subject is required")
    @Size(max = 255)
    String subject,

    @Size(max = 2000)
    String description,

    @NotNull(message = "Scheduled at date is required")
    LocalDateTime scheduledAt,

    @NotNull(message = "User ID is required")
    UUID userId
) {}
