package com.dxc.projectservice.application.port.in;

import com.dxc.projectservice.application.dto.milestones.req.CreateMilestoneRequest;
import com.dxc.projectservice.application.dto.milestones.req.UpdateMilestoneRequest;
import com.dxc.projectservice.application.dto.milestones.res.MilestoneResponse;

import java.util.UUID;

public interface MilestoneUseCase {
    MilestoneResponse addMilestone(UUID projectId, CreateMilestoneRequest request);
    void removeMilestone(UUID projectId, UUID milestoneId);
    MilestoneResponse updateMilestone(UUID projectId, UUID milestoneId, UpdateMilestoneRequest request);
    MilestoneResponse completeMilestone(UUID projectId, UUID milestoneId);
}
