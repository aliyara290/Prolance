package com.dxc.tenantservice.domain.model.tenant;

import com.dxc.tenantservice.domain.model.enums.Language;
import com.dxc.tenantservice.domain.model.enums.PlanStatus;
import com.dxc.tenantservice.domain.model.enums.PlanType;
import com.dxc.tenantservice.domain.model.record.PlanDefaults;
import lombok.Getter;
import java.util.UUID;

@Getter
public class TenantSettings {

    private final UUID id;
    private final UUID tenantId;

    private PlanType plan;
    private PlanStatus planStatus;

    private int maxUsers;
    private int maxProjects;
    private int maxTasksPerProject;

    private boolean enableNotifications;
    private boolean twoFactorRequired;

    private String timezone;
    private Language language;
    private String dateFormat;

    private TenantSettings(
            UUID id,
            UUID tenantId,
            PlanType plan,
            PlanStatus planStatus,
            int maxUsers,
            int maxProjects,
            int maxTasksPerProject,
            boolean enableNotifications,
            boolean twoFactorRequired,
            String timezone,
            Language language,
            String dateFormat
    ) {
        validate(tenantId, plan, planStatus);
        this.id = id;
        this.tenantId = tenantId;
        this.plan = plan;
        this.planStatus = planStatus;
        this.maxUsers = maxUsers;
        this.maxProjects = maxProjects;
        this.maxTasksPerProject = maxTasksPerProject;
        this.enableNotifications = enableNotifications;
        this.twoFactorRequired = twoFactorRequired;
        this.timezone = timezone;
        this.language = language;
        this.dateFormat = dateFormat;
    }

    public static TenantSettings createDefault(UUID tenantId) {
        return createForPlan(tenantId, PlanType.FREE);
    }

    public static TenantSettings createForPlan(UUID tenantId, PlanType plan) {
        PlanDefaults defaults = PlanDefaults.from(plan);

        return new TenantSettings(
                UUID.randomUUID(),
                tenantId,
                plan,
                PlanStatus.ACTIVE,
                defaults.maxUsers(),
                defaults.maxProjects(),
                defaults.maxTasksPerProject(),
                true,
                false,
                "UTC",
                Language.EN,
                "yyyy-MM-dd"
        );
    }

    public void upgradePlan(PlanType newPlan) {
        PlanDefaults defaults = PlanDefaults.from(newPlan);

        this.plan = newPlan;
        this.maxUsers = defaults.maxUsers();
        this.maxProjects = defaults.maxProjects();
        this.maxTasksPerProject = defaults.maxTasksPerProject();
    }

    private void validate(UUID tenantId, PlanType plan, PlanStatus status) {
        if (tenantId == null) throw new IllegalArgumentException("tenantId is required");
        if (plan == null) throw new IllegalArgumentException("plan is required");
        if (status == null) throw new IllegalArgumentException("planStatus is required");
    }
}