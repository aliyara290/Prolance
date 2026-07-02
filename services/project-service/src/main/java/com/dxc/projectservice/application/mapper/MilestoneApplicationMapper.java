package com.dxc.projectservice.application.mapper;

import com.dxc.projectservice.application.dto.milestones.res.MilestoneResponse;
import com.dxc.projectservice.domain.model.entity.Milestone;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MilestoneApplicationMapper {

    public MilestoneResponse toResponse(Milestone milestone) {
        if (milestone == null) return null;
        
        return new MilestoneResponse(
            milestone.getId(),
            milestone.getProjectId(),
            milestone.getTitle(),
            milestone.getDescription(),
            milestone.getStatus(),
            milestone.getStartDate(),
            milestone.getDueDate(),
            milestone.getCompletedAt(),
            milestone.getSequenceOrder(),
            milestone.getProgressPercentage(),
            milestone.getCreatedAt(),
            milestone.getUpdatedAt()
        );
    }
    
    public List<MilestoneResponse> toResponseList(List<Milestone> milestones) {
        if (milestones == null) return null;
        return milestones.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
