package com.dxc.taskservice.application.dto.projectdashboard;

import java.util.UUID;

public record AssigneeWorkloadDto(UUID userId, long activeTaskCount) {}
