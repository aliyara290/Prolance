package com.dxc.projectservice.application.service;

import com.dxc.projectservice.application.dto.milestones.req.CreateMilestoneRequest;
import com.dxc.projectservice.application.dto.milestones.req.UpdateMilestoneRequest;
import com.dxc.projectservice.application.dto.milestones.res.MilestoneResponse;
import com.dxc.projectservice.application.mapper.MilestoneApplicationMapper;
import com.dxc.projectservice.application.port.in.MilestoneUseCase;
import com.dxc.projectservice.application.port.out.DomainEventPublisher;
import com.dxc.projectservice.application.port.out.ProjectRepository;
import com.dxc.projectservice.application.security.TenantGuard;
import com.dxc.projectservice.domain.exception.RecordNotFoundException;
import com.dxc.projectservice.domain.model.aggregate.Project;
import com.dxc.projectservice.domain.model.entity.Milestone;
import com.dxc.projectservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MilestoneService implements MilestoneUseCase {

    private final ProjectRepository projectRepository;
    private final DomainEventPublisher eventPublisher;
    private final MilestoneApplicationMapper milestoneMapper;
    private final TenantGuard tenantGuard;

    @Override
    @Transactional
    public MilestoneResponse addMilestone(UUID projectId, CreateMilestoneRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();
        
        Project project = getProject(projectId, tenantId);
        project.addMilestone(request.title(), request.description(), request.startDate(), request.dueDate(), request.sequenceOrder(), actionBy);
        
        projectRepository.save(project);
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();
        
        // Find the most recently added milestone by looking at the highest order or title
        Milestone addedMilestone = project.getMilestones().stream()
                .filter(m -> m.getTitle().equals(request.title()))
                .max(Comparator.comparing(Milestone::getCreatedAt))
                .orElseThrow();
                
        return milestoneMapper.toResponse(addedMilestone);
    }

    @Override
    @Transactional
    public void removeMilestone(UUID projectId, UUID milestoneId) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();
        
        Project project = getProject(projectId, tenantId);
        project.deleteMilestone(milestoneId, actionBy);
        
        projectRepository.save(project);
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();
    }

    @Override
    @Transactional
    public MilestoneResponse updateMilestone(UUID projectId, UUID milestoneId, UpdateMilestoneRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();
        
        Project project = getProject(projectId, tenantId);
        project.updateMilestone(milestoneId, request.title(), request.description(), request.startDate(), request.dueDate(), actionBy);
        
        projectRepository.save(project);
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();
        
        Milestone updatedMilestone = project.getMilestones().stream()
                .filter(m -> m.getId().equals(milestoneId))
                .findFirst()
                .orElseThrow();
                
        return milestoneMapper.toResponse(updatedMilestone);
    }

    @Override
    @Transactional
    public MilestoneResponse completeMilestone(UUID projectId, UUID milestoneId) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();
        
        Project project = getProject(projectId, tenantId);
        project.completeMilestone(milestoneId, actionBy);
        
        projectRepository.save(project);
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();
        
        Milestone updatedMilestone = project.getMilestones().stream()
                .filter(m -> m.getId().equals(milestoneId))
                .findFirst()
                .orElseThrow();
                
        return milestoneMapper.toResponse(updatedMilestone);
    }

    private Project getProject(UUID id, UUID tenantId) {
        return projectRepository.findById(id, tenantId)
                .orElseThrow(() -> new RecordNotFoundException("Project not found with id " + id));
    }

    private UUID getTenantIdAndVerify() {
        String tenantIdStr = TenantContextHolder.getTenantId();
        if (tenantIdStr == null) {
            throw new IllegalArgumentException("Tenant context is missing");
        }
        UUID tenantId = UUID.fromString(tenantIdStr);
        tenantGuard.ensureTenantIsActive(tenantId);
        return tenantId;
    }
}
