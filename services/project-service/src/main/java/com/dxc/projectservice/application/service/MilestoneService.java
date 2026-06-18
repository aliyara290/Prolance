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
import com.dxc.projectservice.domain.model.valueobject.MilestoneStatus;
import com.dxc.projectservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MilestoneService implements MilestoneUseCase {

    private final ProjectRepository projectRepository;
    private final DomainEventPublisher eventPublisher;
    private final MilestoneApplicationMapper milestoneMapper;
    private final TenantGuard tenantGuard;
    private final com.dxc.projectservice.application.port.out.MilestoneRepository milestoneRepository;

    @Override
    @Transactional
    public MilestoneResponse addMilestone(UUID projectId, CreateMilestoneRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();
        
        Project project = getProject(projectId, tenantId);
        project.addMilestone(request.title(), request.description(), request.startDate(), request.dueDate(), request.sequenceOrder(), request.progressPercentage(), request.status(), actionBy);
        
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
        project.updateMilestone(milestoneId, request.title(), request.description(), request.startDate(), request.dueDate(), request.sequenceOrder(), request.progressPercentage(), request.status(), actionBy);
        
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

    @Override
    @Transactional(readOnly = true)
    public Page<MilestoneResponse> getMilestones(UUID projectId, Pageable pageable) {
        UUID tenantId = getTenantIdAndVerify();
        Project project = getProject(projectId, tenantId);
        List<MilestoneResponse> responses = project.getMilestones().stream()
                .map(milestoneMapper::toResponse)
                .toList();
                
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        List<MilestoneResponse> pageContent = (start <= end && start < responses.size()) 
            ? responses.subList(start, end) 
            : java.util.Collections.emptyList();
            
        return new PageImpl<>(pageContent, pageable, responses.size());
    }

    @Override
    @Transactional(readOnly = true)
    public MilestoneResponse getMilestoneById(UUID projectId, UUID milestoneId) {
        UUID tenantId = getTenantIdAndVerify();
        Project project = getProject(projectId, tenantId);
        Milestone milestone = project.getMilestones().stream()
                .filter(m -> m.getId().equals(milestoneId))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Milestone not found with id " + milestoneId));
        return milestoneMapper.toResponse(milestone);
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

    @Override
    @Transactional(readOnly = true)
    public Page<MilestoneResponse> getAllTenantMilestones(Pageable pageable) {
        UUID tenantId = getTenantIdAndVerify();
        return milestoneRepository.findByTenantId(tenantId, pageable)
                .map(milestoneMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MilestoneResponse> getTenantMilestonesByStatus(com.dxc.projectservice.domain.model.valueobject.MilestoneStatus status, Pageable pageable) {
        UUID tenantId = getTenantIdAndVerify();
        return milestoneRepository.findByTenantIdAndStatus(tenantId, status, pageable)
                .map(milestoneMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MilestoneResponse> getOverdueMilestones(Pageable pageable) {
        UUID tenantId = getTenantIdAndVerify();
        return milestoneRepository.findOverdueMilestones(tenantId, java.time.LocalDateTime.now(), pageable)
                .map(milestoneMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MilestoneResponse> getUpcomingMilestones(Pageable pageable) {
        UUID tenantId = getTenantIdAndVerify();
        return milestoneRepository.findUpcomingMilestones(tenantId, java.time.LocalDateTime.now(), pageable)
                .map(milestoneMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public com.dxc.projectservice.application.dto.milestones.res.MilestoneStatisticsResponse getMilestoneStatistics() {
        UUID tenantId = getTenantIdAndVerify();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        long total = milestoneRepository.countByTenantId(tenantId);
        long completed = milestoneRepository.countByTenantIdAndStatus(tenantId, MilestoneStatus.COMPLETED);
        long overdue = milestoneRepository.countOverdue(tenantId, now);
        
        long active = milestoneRepository.countByTenantIdAndStatus(tenantId, MilestoneStatus.ACTIVE)
                      + milestoneRepository.countByTenantIdAndStatus(tenantId, MilestoneStatus.IN_PROGRESS);
        
        return new com.dxc.projectservice.application.dto.milestones.res.MilestoneStatisticsResponse(total, completed, active, overdue);
    }
}
