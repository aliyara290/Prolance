package com.dxc.projectservice.application.port.in;

import com.dxc.projectservice.application.dto.member.req.AddMemberRequest;
import com.dxc.projectservice.application.dto.member.req.UpdateMemberRequest;
import com.dxc.projectservice.application.dto.member.res.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MemberUseCase {
    MemberResponse addMember(UUID projectId, AddMemberRequest request);
    void removeMember(UUID projectId, UUID userId);
    MemberResponse updateMember(UUID projectId, UUID userId, UpdateMemberRequest request);
    Page<MemberResponse> getMembers(UUID projectId, Pageable pageable);
    MemberResponse getMemberById(UUID projectId, UUID userId);
}
