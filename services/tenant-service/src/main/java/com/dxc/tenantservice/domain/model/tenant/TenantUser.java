package com.dxc.tenantservice.domain.model.tenant;

import com.dxc.tenantservice.domain.exception.UserStateException;
import com.dxc.tenantservice.domain.exception.UserValidationException;
import com.dxc.tenantservice.domain.model.valueobject.UserStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Slf4j
public class TenantUser {

    private final UUID id;
    private final UUID tenantId;

    private UUID keycloakUserId;
    private final Set<UUID>  keycloakRoleGroupIds;

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String department;

    private UserStatus status;

    private LocalDateTime lastLoginAt;
    private UserPreference userPreference;

    private LocalDateTime deleted_at;

    public TenantUser(
            UUID id,
            UUID tenantId,
            UUID keycloakUserId,
            Set<UUID> keycloakRoleGroupIds,
            String email,
            String username,
            String firstName,
            String lastName,
            String jobTitle,
            String department,
            UserStatus status,
            LocalDateTime lastLoginAt,
            UserPreference userPreference
    ) {
        validate(tenantId, email, firstName);

        this.id = id;
        this.tenantId = tenantId;
        this.keycloakUserId = keycloakUserId;
        this.email = email;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.department = department;
        this.keycloakRoleGroupIds = keycloakRoleGroupIds != null ? new HashSet<>(keycloakRoleGroupIds) : new HashSet<>();
        this.status = status != null ? status : UserStatus.PENDING;
        this.lastLoginAt = lastLoginAt;
        this.userPreference = userPreference;
    }

    public static TenantUser create(
            UUID tenantId,
            String email,
            String username,
            String firstName,
            String lastName
    ) {
        log.info("user email |||||-----: {}", email);
        return new TenantUser(
                UUID.randomUUID(),
                tenantId,
                null, // keycloakUserId
                null, // keycloakRoleGroupIds
                email,
                username,
                firstName,
                lastName,
                null, // jobTitle
                null, // department
                UserStatus.PENDING,
                null, // lastLoginAt
                null  // userPreference
        );
    }

    /**
     * Returns an unmodifiable view of the role group IDs.
     * Use {@link #addRoleGroup(UUID)} and {@link #removeRoleGroup(UUID)} to mutate.
     */
    public Set<UUID> getKeycloakRoleGroupIds() {
        return Collections.unmodifiableSet(keycloakRoleGroupIds);
    }

    public void addRoleGroup(UUID roleGroupId) {
        if (roleGroupId == null) {
            throw new UserValidationException("Role group ID cannot be null");
        }
        if (!keycloakRoleGroupIds.add(roleGroupId)) {
            throw new UserStateException("User already has role group: " + roleGroupId);
        }
    }

    public void removeRoleGroup(UUID roleGroupId) {
        if (roleGroupId == null) {
            throw new UserValidationException("Role group ID cannot be null");
        }
        if (!keycloakRoleGroupIds.remove(roleGroupId)) {
            throw new UserStateException("User does not have role group: " + roleGroupId);
        }
    }

    public void addUserPreference(UserPreference userPreference) {
        if (this.userPreference != null) {
            throw new UserStateException("User preference already exists");
        }
        if (userPreference == null) {
            throw new UserValidationException("User preference cannot be null");
        }
        this.userPreference = userPreference;
    }

    public void assignKeycloakUser(UUID keycloakUserId) {
        if (keycloakUserId == null) {
            throw new UserValidationException("Keycloak userId cannot be null");
        }
        if (this.keycloakUserId != null) {
            throw new UserStateException("Keycloak user already assigned");
        }
        this.keycloakUserId = keycloakUserId;
    }

    public void activate() {
        if (this.status != UserStatus.PENDING && this.status != UserStatus.SUSPENDED && this.status != UserStatus.INACTIVE) {
            throw new UserStateException("User cannot be activated from state: " + status);
        }
        this.status = UserStatus.ACTIVE;
    }

    public void suspend() {
        if (this.status != UserStatus.ACTIVE) {
            throw new UserStateException("Only active users can be suspended");
        }
        this.status = UserStatus.SUSPENDED;
    }

    public void deactivate() {
        if (this.status == UserStatus.INACTIVE) {
            throw new UserStateException("User is already inactive");
        }
        this.status = UserStatus.INACTIVE;
    }

    public void ban() {
        if (this.status == UserStatus.BANNED) {
            throw new UserStateException("User already banned");
        }
        this.status = UserStatus.BANNED;
    }

    public void updateProfile(
            String firstName,
            String lastName,
            String jobTitle,
            String department
    ) {
        if (firstName == null || firstName.isBlank()) {
            throw new UserValidationException("First name is required");
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.department = department;
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    private void validate(UUID tenantId, String email, String firstName) {
        if (tenantId == null) {
            throw new UserValidationException("tenantId is required");
        }

        if (email == null || !email.contains("@")) {
            throw new UserValidationException("Invalid email");
        }

        if (firstName == null || firstName.isBlank()) {
            throw new UserValidationException("First name is required");
        }
    }
}