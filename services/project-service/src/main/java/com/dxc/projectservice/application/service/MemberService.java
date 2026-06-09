package com.dxc.projectservice.application.service;

import com.dxc.projectservice.application.dto.member.req.AddMemberRequest;
import com.dxc.projectservice.application.dto.member.req.UpdateMemberRoleRequest;
import com.dxc.projectservice.application.dto.member.res.MemberResponse;
import com.dxc.projectservice.application.mapper.MemberApplicationMapper;
import com.dxc.projectservice.application.port.in.MemberUseCase;
import com.dxc.projectservice.application.port.out.DomainEventPublisher;
import com.dxc.projectservice.application.port.out.ProjectRepository;
import com.dxc.projectservice.application.security.TenantGuard;
import com.dxc.projectservice.domain.exception.RecordNotFoundException;
import com.dxc.projectservice.domain.model.aggregate.Project;
import com.dxc.projectservice.domain.model.entity.Member;
import com.dxc.projectservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberUseCase {

    private final ProjectRepository projectRepository;
    private final DomainEventPublisher eventPublisher;
    private final MemberApplicationMapper memberMapper;
    private final TenantGuard tenantGuard;

    @Override
    @Transactional
    public MemberResponse addMember(UUID projectId, AddMemberRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();
        
        Project project = getProject(projectId, tenantId);
        project.addMember(request.userId(), request.role(), request.allocationPercentage(), actionBy);
        
        projectRepository.save(project);
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();
        
        Member addedMember = project.getMembers().stream()
                .filter(m -> m.getUserId().equals(request.userId()))
                .findFirst()
                .orElseThrow();
                
        return memberMapper.toResponse(addedMember);
    }

    @Override
    @Transactional
    public void removeMember(UUID projectId, UUID userId) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();
        
        Project project = getProject(projectId, tenantId);
        project.removeMember(userId, actionBy);
        
        projectRepository.save(project);
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();
    }

    @Override
    @Transactional
    public MemberResponse updateMemberRole(UUID projectId, UUID userId, UpdateMemberRoleRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();
        
        Project project = getProject(projectId, tenantId);
        project.updateMemberRole(userId, request.role(), actionBy);
        
        projectRepository.save(project);
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();
        
        Member updatedMember = project.getMembers().stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst()
                .orElseThrow();
                
        return memberMapper.toResponse(updatedMember);
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
