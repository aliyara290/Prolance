package com.dxc.tenantservice.infrastructure.adapter.out.keycloak;

import com.dxc.tenantservice.application.dto.user.req.CreateUserReqDTO;
import com.dxc.tenantservice.application.port.out.keycloak.KeycloakPort;
import com.dxc.tenantservice.domain.exception.KeycloakIntegrationException;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.client.*;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.groups.CreateKeycloakGroupReqDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.groups.KeycloakGroupResDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.keycloak.KeycloakTokenResDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.role.KeycloakRoleReqDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.role.KeycloakRoleResDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.users.KeycloakUserReqDTO;
import com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.users.KeycloakUserResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakAdapter implements KeycloakPort {
    private final KeycloakUserClient userClient;
    private final KeycloakGroupClient groupClient;
    private final KeycloakTokenClient tokenClient;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Override
    public String createUser(KeycloakUserReqDTO userDto, UUID tenantId) {
        try {
            log.info("Creating user in Keycloak: email={}, tenantId={}", userDto.getEmail(), tenantId);

            log.debug("Sending user creation request to Keycloak");
            ResponseEntity<Void> response = userClient.createUser(userDto);

            if (response.getStatusCode().is2xxSuccessful()) {
                String location = response.getHeaders().getLocation().getPath();
                String userId = location.substring(location.lastIndexOf('/') + 1);
                log.info("User created successfully in Keycloak: userId={}", userId);
                return userId;
            } else {
                log.error("Keycloak user creation failed with status: {}", response.getStatusCode());
                throw new KeycloakIntegrationException("Failed to create user in Keycloak: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error creating user in Keycloak: email={}, error={}", userDto.getEmail(), e.getMessage(), e);
            throw new KeycloakIntegrationException("Failed to create user in Keycloak: " + e.getMessage(), e);
        }
    }


    @Override
    public String createUserWithEmailVerification(KeycloakUserReqDTO userDto, UUID tenantId) {
        try {
            log.info("Creating user with email verification in Keycloak: email={}, tenantId={}", userDto.getEmail(), tenantId);


            log.debug("Sending user creation request to Keycloak");
            ResponseEntity<Void> response = userClient.createUser(userDto);

            if (response.getStatusCode().is2xxSuccessful()) {
                String location = response.getHeaders().getLocation().getPath();
                String userId = location.substring(location.lastIndexOf('/') + 1);
                log.info("User created successfully in Keycloak: userId={}", userId);

                try {
                    log.info("Sending email verification with UPDATE_PASSWORD action to user: {}", userDto.getEmail());
                    userClient.executeActionsEmail(userId, clientId, 43200, List.of("UPDATE_PASSWORD", "VERIFY_EMAIL"));
                    log.info("Email verification sent successfully to user: {}", userDto.getEmail());
                } catch (Exception emailException) {
                    log.warn("Failed to send email verification to user: {}. User created successfully but email not sent. Error: {}",
                            userDto.getEmail(), emailException.getMessage());
                    log.warn("Please ensure Keycloak SMTP is configured or manually send verification email from Keycloak admin console.");
                }

                return userId;
            } else {
                log.error("Keycloak user creation failed with status: {}", response.getStatusCode());
                throw new KeycloakIntegrationException("Failed to create user in Keycloak: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error creating user with email verification in Keycloak: email={}, error={}", userDto.getEmail(), e.getMessage(), e);
            throw new KeycloakIntegrationException("Failed to create user with email verification in Keycloak: " + e.getMessage(), e);
        }
    }

    @Override
    public UUID createCompanyGroup(UUID tenantId) {
        String tenantGroupName = "tenant-" + tenantId;
        log.info("Creating company group structure in Keycloak: tenantId={}, groupName={}", tenantId, tenantGroupName);

        CreateKeycloakGroupReqDTO tenantGroup = new CreateKeycloakGroupReqDTO();
        tenantGroup.setName(tenantGroupName);

        log.debug("Creating parent group: {}", tenantGroupName);
        ResponseEntity<Void> response = groupClient.createGroup(tenantGroup);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to create parent group with status: {}", response.getStatusCode());
            throw new KeycloakIntegrationException("Failed to create parent group: " + response.getStatusCode());
        }

        String location = response.getHeaders().getLocation().getPath();
        String tenantGroupId = location.substring(location.lastIndexOf('/') + 1);
        log.info("Parent group created: tenantGroupId={}", tenantGroupId);

        return UUID.fromString(tenantGroupId);
    }

    @Override
    public void createCompanySubGroup(UUID parentGroupId, String subGroupName) {
        log.info("Creating role subgroup: {}", subGroupName);
        CreateKeycloakGroupReqDTO roleGroup = new CreateKeycloakGroupReqDTO();
        roleGroup.setName(subGroupName);
        ResponseEntity<Void> subgroupResponse = groupClient.createSubgroup(parentGroupId, roleGroup);
        String subgroupLocation = subgroupResponse.getHeaders().getLocation().toString();
        String subgroupId = subgroupLocation.substring(subgroupLocation.lastIndexOf("/") + 1);

        if (subgroupResponse.getStatusCode().is2xxSuccessful()) {
            log.info("Subgroup created successfully: {}", subGroupName);
            KeycloakRoleResDTO getRealmRole = groupClient.getRealmRole(subGroupName);
            log.debug("keycloak real role: {}", getRealmRole);
            KeycloakRoleReqDTO attachingBody = new KeycloakRoleReqDTO();
            attachingBody.setId(getRealmRole.getId());
            attachingBody.setName(getRealmRole.getName());
            groupClient.attachRoleToGroup(subgroupId, List.of(attachingBody));
        } else {
            log.error("Failed to create subgroup {} with status: {}", subGroupName, subgroupResponse.getStatusCode());
            throw new KeycloakIntegrationException("Failed to create subgroup: " + subGroupName);
        }
    }

    @Override
    public UUID assignUserToGroup(String keycloakUserId, UUID tenantId, String role) {
        try {
            log.info("Assigning user to group: userId={}, tenantId={}, role={}", keycloakUserId, tenantId, role);
            UUID roleGroupId = findRoleGroupId(tenantId, role);
            userClient.addUserToGroup(keycloakUserId, roleGroupId);
            log.info("User assigned to group successfully: userId={}, groupId={}", keycloakUserId, roleGroupId);
            return roleGroupId;
        } catch (Exception e) {
            log.error("Error assigning user to group: userId={}, tenantId={}, role={}, error={}",
                    keycloakUserId, tenantId, role, e.getMessage(), e);
            throw new KeycloakIntegrationException("Failed to assign user to group in Keycloak: " + e.getMessage(), e);
        }
    }

    @Override
    public void changeUserRole(String keycloakUserId, UUID tenantId, UUID oldRoleGroupId, String newRole) {
        try {
            log.info("Changing user group: userId={}, tenantId={}, oldRole={}, newRole={}",
                    keycloakUserId, tenantId, oldRoleGroupId, newRole);

            UUID newRoleGroupId = findRoleGroupId(tenantId, newRole);

            userClient.removeUserFromGroup(keycloakUserId, oldRoleGroupId);
            userClient.addUserToGroup(keycloakUserId, newRoleGroupId);

            log.info("User group changed successfully: userId={}, oldGroupId={}, newGroupId={}",
                    keycloakUserId, oldRoleGroupId, newRoleGroupId);
        } catch (Exception e) {
            log.error("Error changing user group: userId={}, tenantId={}, error={}",
                    keycloakUserId, tenantId, e.getMessage(), e);
            throw new KeycloakIntegrationException("Failed to change user group in Keycloak: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeUserFromGroup(String keycloakUserId, UUID oldRoleGroupId, String role) {
        try {
            log.info("Removing user from all groups: userId={}, oldRoleGroupId={}", keycloakUserId, oldRoleGroupId);

                try {
                    UUID roleGroupId = findRoleGroupId(oldRoleGroupId, role);
                    userClient.removeUserFromGroup(keycloakUserId, roleGroupId);
                } catch (Exception e) {
                    log.warn("Could not remove user from role group: role={}, error={}", role, e.getMessage());
                }

            log.info("User removed from all groups: userId={}", keycloakUserId);
        } catch (Exception e) {
            log.error("Error removing user from groups: userId={}, oldRoleGroupId={}, error={}",
                    keycloakUserId, oldRoleGroupId, e.getMessage(), e);
            throw new KeycloakIntegrationException("Failed to remove user from groups in Keycloak: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteUser(String keycloakUserId) {
        try {
            log.info("Deleting user from Keycloak: userId={}", keycloakUserId);
            userClient.deleteUser(keycloakUserId);
            log.info("User deleted successfully: userId={}", keycloakUserId);
        } catch (Exception e) {
            log.error("Error deleting user: userId={}, error={}", keycloakUserId, e.getMessage(), e);
            throw new KeycloakIntegrationException("Failed to delete user in Keycloak: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteGroup(UUID groupId) {
        try {
            log.info("Deleting group from Keycloak: groupId={}", groupId);
            groupClient.deleteGroup(groupId.toString());
            log.info("Group deleted successfully: groupId={}", groupId);
        } catch (Exception e) {
            log.error("Error deleting group: groupId={}, error={}", groupId, e.getMessage(), e);
            throw new KeycloakIntegrationException("Failed to delete group in Keycloak: " + e.getMessage(), e);
        }
    }

    @Override
    public KeycloakTokenResDTO getUserAccessToken(String email, String password) {
        try {
            log.info("Obtaining user access token: email={}", email);
            KeycloakTokenResDTO response = tokenClient.getUserToken(
                    realm,
                    "password",
                    email,
                    password,
                    clientId
            );
            log.info("User access token obtained successfully: email={}", email);
            return response;
        } catch (Exception e) {
            log.error("Error obtaining user access token: email={}, error={}", email, e.getMessage(), e);
            throw new KeycloakIntegrationException("Failed to obtain user access token: " + e.getMessage(), e);
        }
    }

    private UUID findRoleGroupId(UUID parentGroupId, String roleGroupName) {
        log.debug("Finding role group: tenantId={}, role={}", parentGroupId, roleGroupName);

        List<KeycloakGroupResDTO> subGroups = groupClient.getGroupChildren(parentGroupId);

        log.info("Tenant group children fetched: groupId={}, subGroupsCount={}",
                parentGroupId,
                subGroups != null ? subGroups.size() : 0);

        if (subGroups == null || subGroups.isEmpty()) {
            log.error("No role subgroups found for tenant: {}", parentGroupId);
            throw new KeycloakIntegrationException("No role subgroups found for tenant: " + parentGroupId);
        }

        UUID roleGroupId = subGroups.stream()
                .filter(subGroup -> subGroup.getName().equals(roleGroupName))
                .map(KeycloakGroupResDTO::getId)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Role group not found: role={}, tenantId={}", roleGroupName, parentGroupId);
                    return new KeycloakIntegrationException("Role group not found: " + roleGroupName);
                });

        log.debug("Found role group: role={}, groupId={}", roleGroupName, roleGroupId);
        return roleGroupId;
    }
}
