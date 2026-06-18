package com.dxc.projectservice.application.dto.milestones.res;

public record MilestoneStatisticsResponse(
    long totalMilestones,
    long completedMilestones,
    long activeMilestones,
    long overdueMilestones
) {
}
