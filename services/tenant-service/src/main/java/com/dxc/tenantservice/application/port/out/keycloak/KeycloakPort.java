package com.dxc.tenantservice.application.port.out.keycloak;

import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.users.KeycloakUserReqDTO;

import java.util.UUID;

public interface KeycloakPort {
    String createUser(KeycloakUserReqDTO userDto, UUID tenantId);
    String createUserWithEmailVerification(KeycloakUserReqDTO userDto, UUID tenantId);
    void createCompanyGroup(UUID tenantId);
    void assignUserToGroup(String keycloakUserId, UUID tenantId, String role);
    void changeUserGroup(String keycloakUserId, UUID tenantId, String oldRole, String newRole);
    void removeUserFromGroup(String keycloakUserId, UUID tenantId);
    void deleteUser(String keycloakUserId);
    String getUserAccessToken(String email, String password);
}