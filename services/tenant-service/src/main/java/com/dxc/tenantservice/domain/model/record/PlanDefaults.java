package com.dxc.tenantservice.domain.model.record;

import com.dxc.tenantservice.domain.model.enums.PlanType;

public record PlanDefaults(
        int maxUsers,
        int maxProjects,
        int maxTasksPerProject
) {
    public static PlanDefaults from(PlanType plan) {
        return switch (plan) {
            case FREE -> new PlanDefaults(5, 10, 100);
            case PRO -> new PlanDefaults(50, 100, 1000);
            case ENTERPRISE -> new PlanDefaults(1000, 10000, 10000);
        };
    }
}