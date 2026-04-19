package com.dxc.tenantservice.domain.model.tenant;

import com.dxc.tenantservice.domain.exception.UserStateException;
import com.dxc.tenantservice.domain.exception.UserValidationException;
import com.dxc.tenantservice.domain.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantUser {
    private UUID id;
    private UUID tenantId;
    private UUID keycloakUserId;
    private UUID keycloakGroupId;
    
    private String email;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String department;
    
    private UserStatus status;
    
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    private UserPreference userPreference;

    public void validate() {
        if (email == null || !email.contains("@")) {
            throw new UserValidationException("Invalid user email format");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new UserValidationException("User first name cannot be empty");
        }
    }

    public void activate() {
        if (this.status == UserStatus.ACTIVE) {
            throw new UserStateException("User is already active");
        }
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void ban() {
        if (this.status == UserStatus.BANNED) {
            throw new UserStateException("User is already banned");
        }
        this.status = UserStatus.BANNED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
