package com.dxc.tenantservice.infrastructure.adapter.out.persistence.entity;

import com.dxc.tenantservice.domain.model.valueobject.PlanStatus;
import com.dxc.tenantservice.domain.model.valueobject.PlanType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tenant_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSettingsEntity extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_status", nullable = false)
    private PlanStatus planStatus;

    @Column(name = "max_users", nullable = false)
    private int maxUsers;

    @Column(name = "max_projects", nullable = false)
    private int maxProjects;

    @Column(name = "max_tasks_per_project", nullable = false)
    private int maxTasksPerProject;

    @Column(name = "enable_notifications", nullable = false)
    private boolean enableNotifications;

    @Column(name = "two_factor_required", nullable = false)
    private boolean twoFactorRequired;

    @Column(length = 50)
    private String timezone;

    @Column(length = 10)
    private String language;

    @Column(name = "date_format", length = 20)
    private String dateFormat;

}
