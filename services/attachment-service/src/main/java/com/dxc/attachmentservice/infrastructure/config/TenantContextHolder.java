package com.dxc.attachmentservice.infrastructure.config;

import java.util.UUID;

public class TenantContextHolder {
    private static final ThreadLocal<String> currentTenantId = new ThreadLocal<>();
    private static final ThreadLocal<UUID> currentKeycloakUserId = new ThreadLocal<>();

    public static UUID getUserId() {
        return currentKeycloakUserId.get();
    }

    public static void setUserId(UUID keycloakUserId) {
        currentKeycloakUserId.set(keycloakUserId);
    }

    public static void clearUserId() {
        currentKeycloakUserId.remove();
    }
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
