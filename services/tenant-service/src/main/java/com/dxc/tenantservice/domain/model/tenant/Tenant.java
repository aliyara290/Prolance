package com.dxc.tenantservice.domain.model.tenant;

import com.dxc.tenantservice.domain.exception.TenantStateException;
import com.dxc.tenantservice.domain.exception.TenantValidationException;
import com.dxc.tenantservice.domain.model.enums.TenantIndustry;
import com.dxc.tenantservice.domain.model.enums.TenantStatus;
import com.dxc.tenantservice.domain.model.valueobjects.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    private UUID id;
    private UUID keycloakGroupId;
    private String name;
    private String email;
    private String website;
    private int size;
    private LocalDate foundedDate;
    private String description;
    private String logo;

    private Address address;
    
    private TenantIndustry industry;
    private TenantStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private TenantSettings settings;
    private List<TenantLog> logs;
    private List<TenantUser> users;

    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new TenantValidationException("Tenant name cannot be empty");
        }
        if (keycloakGroupId == null || name.trim().isEmpty()) {
            throw new TenantValidationException("Keycloak group ID cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new TenantValidationException("Invalid email format");
        }
    }

    public void activate() {
        if (this.status == TenantStatus.ACTIVE) {
            throw new TenantStateException("Tenant is already active");
        }
        this.status = TenantStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        if (this.status == TenantStatus.SUSPENDED) {
            throw new TenantStateException("Tenant is already suspended");
        }
        this.status = TenantStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }
}
