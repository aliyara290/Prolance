package com.dxc.tenantservice.domain.model.tenant;

import com.dxc.tenantservice.domain.exception.UserStateException;
import com.dxc.tenantservice.domain.exception.UserValidationException;
import com.dxc.tenantservice.domain.model.enums.UserStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TenantUser {

    private final UUID id;
    private final UUID tenantId;

    private UUID keycloakUserId;
    private UUID keycloakGroupId;

    private String email;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String department;

    private UserStatus status;

    private LocalDateTime lastLoginAt;

    private TenantUser(
            UUID id,
            UUID tenantId,
            String email,
            String firstName,
            String lastName
    ) {
        validate(tenantId, email, firstName);

        this.id = id;
        this.tenantId = tenantId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = UserStatus.PENDING; // important
    }

    public static TenantUser create(
            UUID tenantId,
            String email,
            String firstName,
            String lastName
    ) {
        return new TenantUser(
                UUID.randomUUID(),
                tenantId,
                email,
                firstName,
                lastName
        );
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

    public void assignToGroup(UUID groupId) {
        if (groupId == null) {
            throw new UserValidationException("GroupId cannot be null");
        }
        this.keycloakGroupId = groupId;
    }

    public void activate() {
        if (this.status != UserStatus.PENDING && this.status != UserStatus.SUSPENDED) {
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