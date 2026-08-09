package com.dxc.projectservice.infrastructure.adapter.in.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneProgressIntegrationEvent {
    private UUID milestoneId;
    private UUID tenantId;
    private float progressPercentage;
}
