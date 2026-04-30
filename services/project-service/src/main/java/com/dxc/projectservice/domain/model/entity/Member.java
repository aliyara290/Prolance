package com.dxc.projectservice.domain.model.entity;

import com.dxc.projectservice.domain.model.valueobject.MemberRole;
import com.dxc.projectservice.domain.model.valueobject.MemberStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class Member {
    private UUID id;
    private UUID tenantId;
    private UUID userId;
    private MemberRole role;
    private int allocationPercentage;
    private MemberStatus status;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
}
