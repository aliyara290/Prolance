package com.dxc.projectservice.application.port.in;

import com.dxc.projectservice.application.dto.milestones.req.CreateMilestoneRequest;
import com.dxc.projectservice.application.dto.milestones.req.UpdateMilestoneRequest;
import com.dxc.projectservice.application.dto.milestones.res.MilestoneResponse;
import com.dxc.projectservice.application.dto.milestones.res.MilestoneStatisticsResponse;
import com.dxc.projectservice.domain.model.valueobject.MilestoneStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MilestoneUseCase {
    MilestoneResponse addMilestone(UUID projectId, CreateMilestoneRequest request);
    void removeMilestone(UUID projectId, UUID milestoneId);
    MilestoneResponse updateMilestone(UUID projectId, UUID milestoneId, UpdateMilestoneRequest request);
    MilestoneResponse completeMilestone(UUID projectId, UUID milestoneId);
    Page<MilestoneResponse> getMilestones(UUID projectId, Pageable pageable);
    MilestoneResponse getMilestoneById(UUID projectId, UUID milestoneId);

    Page<MilestoneResponse> getAllTenantMilestones(Pageable pageable);
    Page<MilestoneResponse> getTenantMilestonesByStatus(MilestoneStatus status, Pageable pageable);
    Page<MilestoneResponse> getOverdueMilestones(Pageable pageable);
    Page<MilestoneResponse> getUpcomingMilestones(Pageable pageable);
    MilestoneStatisticsResponse getMilestoneStatistics();
}
