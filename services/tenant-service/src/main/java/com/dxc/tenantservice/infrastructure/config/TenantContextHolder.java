package com.dxc.tenantservice.infrastructure.config;

public class TenantContextHolder {
    private static final ThreadLocal<String> currentTenantId = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        currentTenantId.set(tenantId);
    }

    public static String getTenantId() {
        return currentTenantId.get();
    }

    public static void clear() {
        currentTenantId.remove();
    }
}
