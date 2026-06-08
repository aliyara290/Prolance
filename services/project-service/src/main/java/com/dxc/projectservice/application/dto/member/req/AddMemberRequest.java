package com.dxc.projectservice.application.dto.member.req;

import com.dxc.projectservice.domain.model.valueobject.MemberRole;
import java.util.UUID;

public record AddMemberRequest(
    UUID userId,
    MemberRole role,
    int allocationPercentage
) {}
