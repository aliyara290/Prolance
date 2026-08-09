package com.dxc.projectservice.application.service;

import com.dxc.projectservice.application.dto.project.req.CreateProjectRequest;
import com.dxc.projectservice.application.dto.project.req.UpdateProjectRequest;
import com.dxc.projectservice.application.dto.project.res.ProjectResponse;
import com.dxc.projectservice.application.dto.project.res.ProjectsNamesResponse;
import com.dxc.projectservice.application.mapper.ProjectApplicationMapper;
import com.dxc.projectservice.application.port.in.ProjectUseCase;
import com.dxc.projectservice.application.port.out.DomainEventPublisher;
import com.dxc.projectservice.application.port.out.ProjectRepository;
import com.dxc.projectservice.application.security.TenantGuard;
import com.dxc.projectservice.domain.exception.RecordNotFoundException;
import com.dxc.projectservice.domain.model.aggregate.Project;
import com.dxc.projectservice.domain.model.valueobject.MemberRole;
import com.dxc.projectservice.domain.model.valueobject.ProjectFinancials;
import com.dxc.projectservice.domain.model.valueobject.MemberStatus;
import com.dxc.projectservice.domain.model.valueobject.ProjectTimeline;
import com.dxc.projectservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService implements ProjectUseCase {

    private final ProjectRepository projectRepository;
    private final DomainEventPublisher eventPublisher;
    private final ProjectApplicationMapper projectMapper;
    private final TenantGuard tenantGuard;
    private final com.dxc.projectservice.infrastructure.adapter.out.persistence.jpa.OutboxEventRepository outboxEventRepository;

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID userId = TenantContextHolder.getUserId();

        ProjectTimeline timeline = new ProjectTimeline(
                request.plannedStartDate(),
                request.plannedEndDate(),
                null,
                null
        );

        ProjectFinancials financials = request.estimatedBudget() != null ? 
                new ProjectFinancials(request.estimatedBudget(), BigDecimal.ZERO) : ProjectFinancials.empty();

        Project project = Project.create(
                tenantId,
                request.clientId(),
                request.opportunityId(),
                request.name(),
                request.description(),
                request.priority(),
                timeline,
                financials,
                request.projectManagerId(),
                request.status(),
                userId
        );

        // Make creator the OWNER
        project.addMember(userId, MemberRole.OWNER, 100, MemberStatus.ACTIVE, userId);

        // Add project manager
        project.addMember(request.projectManagerId(), MemberRole.MANAGER, 100, MemberStatus.ACTIVE, userId);

        Project savedProject = projectRepository.save(project);
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();

        return projectMapper.toResponse(savedProject);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(UUID id) {
        UUID tenantId = getTenantIdAndVerify();
        Project project = getProject(id, tenantId);
        return projectMapper.toResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProjects(Pageable pageable) {
        UUID tenantId = getTenantIdAndVerify();
        Page<Project> projects = projectRepository.findAll(tenantId, pageable);
        return projects.map(projectMapper::toResponse);
    }

    @Override
    public List<ProjectsNamesResponse> getProjectsNames(Pageable pageable) {
        UUID tenantId = getTenantIdAndVerify();

        Page<Project> projects = projectRepository.findAll(tenantId, pageable);

        return projects.stream()
                .map(project -> new ProjectsNamesResponse(
                        project.getId(),
                        project.getName(),
                        project.getPrefix(),
                        project.getStatus()
                ))
                .toList();
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(UUID id, UpdateProjectRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID userId = TenantContextHolder.getUserId();
        
        Project project = getProject(id, tenantId);
        
        project.updateProject(
            request.name(),
            request.description(),
            request.clientId(),
            request.opportunityId(),
            request.priority(),
            request.plannedStartDate(),
            request.plannedEndDate(),
            request.estimatedBudget(),
            request.projectManagerId(),
            request.status(),
            userId
        );        
        Project savedProject = projectRepository.save(project);
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();

        return projectMapper.toResponse(savedProject);
    }

    @Override
    @Transactional
    public void deleteProject(UUID id) {
        UUID tenantId = getTenantIdAndVerify();
        UUID userId = TenantContextHolder.getUserId();
        
        Project project = getProject(id, tenantId);
        
        project.deleteProject(userId);
        projectRepository.delete(id, tenantId);
        
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();
    }
    
    private Project getProject(UUID id, UUID tenantId) {
        return projectRepository.findById(id, tenantId)
                .orElseThrow(() -> new RecordNotFoundException("Project not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.dxc.projectservice.application.dto.project.ProjectActivityDto> getProjectActivities(UUID projectId) {
        UUID tenantId = getTenantIdAndVerify();
        // verify project exists and belongs to tenant
        getProject(projectId, tenantId);

        return outboxEventRepository.findByAggregateIdOrderByOccurredOnDesc(projectId)
                .stream()
                .map(event -> new com.dxc.projectservice.application.dto.project.ProjectActivityDto(
                        event.getId(),
                        event.getType(),
                        event.getPayload(),
                        event.getOccurredOn()
                ))
                .toList();
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
