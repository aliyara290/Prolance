package com.dxc.projectservice.application.port.in;

import com.dxc.projectservice.application.dto.member.req.AddMemberRequest;
import com.dxc.projectservice.application.dto.member.req.UpdateMemberRoleRequest;
import com.dxc.projectservice.application.dto.member.res.MemberResponse;

import java.util.UUID;

public interface MemberUseCase {
    MemberResponse addMember(UUID projectId, AddMemberRequest request);
    void removeMember(UUID projectId, UUID userId);
    MemberResponse updateMemberRole(UUID projectId, UUID userId, UpdateMemberRoleRequest request);
}
