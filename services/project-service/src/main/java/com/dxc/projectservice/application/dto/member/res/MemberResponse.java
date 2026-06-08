package com.dxc.projectservice.application.dto.member.res;

import com.dxc.projectservice.domain.model.valueobject.MemberRole;
import com.dxc.projectservice.domain.model.valueobject.MemberStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record MemberResponse(
    UUID id,
    UUID projectId,
    UUID userId,
    MemberRole role,
    int allocationPercentage,
    MemberStatus status,
    LocalDateTime joinedAt,
    LocalDateTime leftAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
