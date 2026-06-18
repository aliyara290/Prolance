package com.dxc.projectservice.application.service;

import com.dxc.projectservice.application.dto.member.req.AddMemberRequest;
import com.dxc.projectservice.application.dto.member.req.UpdateMemberRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        project.addMember(request.userId(), request.role(), request.allocationPercentage(), request.status(), actionBy);
        
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
    public MemberResponse updateMember(UUID projectId, UUID userId, UpdateMemberRequest request) {
        UUID tenantId = getTenantIdAndVerify();
        UUID actionBy = TenantContextHolder.getUserId();
        
        Project project = getProject(projectId, tenantId);
        project.updateMember(userId, request.role(), request.allocationPercentage(), request.status(), actionBy);
        
        projectRepository.save(project);
        eventPublisher.publish(project.getDomainEvents());
        project.clearDomainEvents();
        
        Member updatedMember = project.getMembers().stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst()
                .orElseThrow();
                
        return memberMapper.toResponse(updatedMember);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberResponse> getMembers(UUID projectId, Pageable pageable) {
        UUID tenantId = getTenantIdAndVerify();
        Project project = getProject(projectId, tenantId);
        List<MemberResponse> responses = project.getMembers().stream()
                .map(memberMapper::toResponse)
                .toList();
                
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        List<MemberResponse> pageContent = (start <= end && start < responses.size()) 
            ? responses.subList(start, end) 
            : java.util.Collections.emptyList();
            
        return new PageImpl<>(pageContent, pageable, responses.size());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberById(UUID projectId, UUID userId) {
        UUID tenantId = getTenantIdAndVerify();
        Project project = getProject(projectId, tenantId);
        Member member = project.getMembers().stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RecordNotFoundException("Member not found with user id " + userId));
        return memberMapper.toResponse(member);
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
