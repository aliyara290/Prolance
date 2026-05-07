package com.dxc.projectservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.projectservice.domain.model.valueobject.MemberRole;
import com.dxc.projectservice.domain.model.valueobject.MemberStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "members")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE members SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class MemberEntity extends BaseAuditingEntity {
    @Id
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "project_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(name = "allocation_percentage", nullable = false)
    private int allocationPercentage;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}
