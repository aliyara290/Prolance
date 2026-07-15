package com.dxc.taskservice.application.dto.projectdashboard;

import com.dxc.taskservice.domain.model.valueobject.TaskPriority;
import com.dxc.taskservice.domain.model.valueobject.TaskStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectTaskDashboardFilterRequest(
        LocalDate startDate,
        LocalDate endDate,
        UUID assigneeId,
        TaskPriority priority,
        TaskStatus status
) {}
