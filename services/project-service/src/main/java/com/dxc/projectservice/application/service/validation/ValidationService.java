package com.dxc.projectservice.application.service.validation;

import com.dxc.projectservice.application.dto.validation.CanCreateTaskInProjectReq;
import com.dxc.projectservice.application.dto.validation.CanCreateTaskInProjectRes;
import com.dxc.projectservice.application.port.in.validation.ValidationUseCase;
import com.dxc.projectservice.application.port.out.ProjectRepository;
import com.dxc.projectservice.application.port.out.feign.UserFeignPort;
import com.dxc.projectservice.application.security.TenantGuard;
import com.dxc.projectservice.domain.model.aggregate.Project;
import com.dxc.projectservice.domain.model.entity.Member;
import com.dxc.projectservice.domain.model.valueobject.MemberStatus;
import com.dxc.projectservice.domain.model.valueobject.ProjectStatus;
import com.dxc.projectservice.infrastructure.config.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService implements ValidationUseCase {

    private final UserFeignPort userFeignPort;
    private final ProjectRepository projectRepository;
    private final TenantGuard tenantGuard;
//    private final MemberRepository memberRepository;

    @Override
    public CanCreateTaskInProjectRes canCreateTaskInProject(UUID projectId, CanCreateTaskInProjectReq req) {
        UUID tenantId = getTenantIdAndVerify();

        Optional<Project> project = projectRepository.findById(projectId, tenantId);

        if (project.isEmpty()) {
            return new CanCreateTaskInProjectRes(false, "Project not found");
        }

        if (project.get().getStatus() == ProjectStatus.CANCELLED) {
            return new CanCreateTaskInProjectRes(false, "Project is cancelled");
        }

        Optional<Member> member = project.get().getMembers()
                .stream()
                .filter(m -> m.getUserId().equals(req.userId()))
                .findAny();

        if(member.isEmpty()) {
            return new CanCreateTaskInProjectRes(false, "User not found");
        }

        if(member.get().getStatus() != MemberStatus.ACTIVE) {
            return new CanCreateTaskInProjectRes(false, "User is not active");
        }

        return new CanCreateTaskInProjectRes(true, null);
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
