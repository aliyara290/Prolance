package com.dxc.billingservice.application.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record LogTimeRequest(
        @NotNull(message = "Project ID is required")
        UUID projectId,
        
        UUID taskId, // Optional
        
        @NotNull(message = "Start time is required")
        LocalDateTime startTime,
        
        @NotNull(message = "End time is required")
        LocalDateTime endTime,
        
        @NotBlank(message = "Description is required")
        String description,
        
        boolean billable
) {}
