package com.dxc.projectservice.application.dto.member.req;

import com.dxc.projectservice.domain.model.valueobject.MemberRole;
import com.dxc.projectservice.domain.model.valueobject.MemberStatus;

public record UpdateMemberRequest(
    MemberRole role,
    Integer allocationPercentage,
    MemberStatus status
) {}
