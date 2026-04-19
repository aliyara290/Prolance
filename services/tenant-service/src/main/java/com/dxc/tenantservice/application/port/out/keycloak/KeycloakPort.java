package com.dxc.tenantservice.application.port.out.keycloak;

import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.keycloak.KeycloakTokenResDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.users.KeycloakUserReqDTO;

import java.util.UUID;

public interface KeycloakPort {
    String createUser(KeycloakUserReqDTO userDto, UUID tenantId);
    String createUserWithEmailVerification(KeycloakUserReqDTO userDto, UUID tenantId);
    UUID createCompanyGroup(UUID tenantId);
    void createCompanySubGroup(UUID parentGroupId, String subGroupName);
    void assignUserToGroup(String keycloakUserId, UUID tenantId, String role);
    void changeUserRole(String keycloakUserId, UUID tenantId, UUID oldRoleGroupId, String newRole);
    void removeUserFromGroup(String keycloakUserId, UUID tenantId, String role);
    void deleteUser(String keycloakUserId);
    KeycloakTokenResDTO getUserAccessToken(String email, String password);
}

