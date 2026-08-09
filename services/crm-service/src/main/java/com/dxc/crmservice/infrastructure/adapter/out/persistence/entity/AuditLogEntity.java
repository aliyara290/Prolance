package com.dxc.crmservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.crmservice.domain.model.valueobject.AuditAction;
import com.dxc.crmservice.domain.model.valueobject.EntityType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity extends BaseAuditingEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(nullable = false)
    private String message;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
