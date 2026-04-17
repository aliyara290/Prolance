package com.dxc.tenantservice.domain.model.tenant;

import com.dxc.tenantservice.domain.model.enums.PlanStatus;
import com.dxc.tenantservice.domain.model.enums.PlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSettings {

    private UUID id;

    private UUID tenantId;
    private PlanType plan;
    private PlanStatus planStatus;

    private int maxUsers;
    private int maxProjects;
    private int maxTasksPerProject;

    private boolean enableNotifications;

    private boolean twoFactorRequired;

    private String timezone;
    private String language;
    private String dateFormat;

    public static TenantSettings createDefault(UUID tenantId) {
        TenantSettings settings = new TenantSettings();

        settings.tenantId = tenantId;
        settings.plan = PlanType.FREE;
        settings.planStatus = PlanStatus.ACTIVE;

        settings.maxUsers = 5;
        settings.maxProjects = 10;
        settings.maxTasksPerProject = 100;

        settings.enableNotifications = true;

        settings.twoFactorRequired = false;

        settings.timezone = "UTC";
        settings.language = "en";
        settings.dateFormat = "yyyy-MM-dd";

        return settings;
    }
}