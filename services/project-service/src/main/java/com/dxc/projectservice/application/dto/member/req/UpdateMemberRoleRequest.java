package com.dxc.projectservice.application.dto.member.req;

import com.dxc.projectservice.domain.model.valueobject.MemberRole;

public record UpdateMemberRoleRequest(
    MemberRole role
) {}
