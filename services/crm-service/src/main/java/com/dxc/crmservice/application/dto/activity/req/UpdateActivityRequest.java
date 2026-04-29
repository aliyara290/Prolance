package com.dxc.crmservice.application.dto.activity.req;

import com.dxc.crmservice.domain.model.valueobject.ActivityType;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateActivityRequest(
    ActivityType type,

    @Size(max = 255)
    String subject,

    @Size(max = 2000)
    String description,

    LocalDateTime scheduledAt,

    UUID userId
) {}
