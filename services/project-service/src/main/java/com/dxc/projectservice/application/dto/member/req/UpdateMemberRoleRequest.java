package com.dxc.projectservice.application.dto.member.req;

import com.dxc.projectservice.domain.model.valueobject.MemberRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
    @NotNull(message = "role is required")
    MemberRole role
) {}
