package com.dxc.tenantservice.domain.model.tenant;

import com.dxc.tenantservice.domain.exception.TenantStateException;
import com.dxc.tenantservice.domain.exception.TenantValidationException;
import com.dxc.tenantservice.domain.model.enums.TenantIndustry;
import com.dxc.tenantservice.domain.model.enums.TenantStatus;
import com.dxc.tenantservice.domain.model.valueobjects.Address;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class Tenant {

    private final UUID id;
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

    private TenantSettings tenantSettings;

    public Tenant(
            UUID id,
            UUID keycloakGroupId,
            String name,
            String email,
            String website,
            int size,
            LocalDate foundedDate,
            String description,
            String logo,
            Address address,
            TenantIndustry industry,
            TenantStatus status,
            TenantSettings tenantSettings
    ) {
        validate(name, email, industry);

        this.id = id;
        this.keycloakGroupId = keycloakGroupId;
        this.name = name;
        this.email = email;
        this.website = website;
        this.size = size;
        this.foundedDate = foundedDate;
        this.description = description;
        this.logo = logo;
        this.address = address;
        this.industry = industry;
        this.status = status != null ? status : TenantStatus.PENDING;
        this.tenantSettings = tenantSettings;
    }

    public static Tenant create(
            UUID id,
            String name,
            String email,
            TenantIndustry industry
    ) {
        return new Tenant(
                id,
                null, // keycloakGroupId
                name,
                email,
                null, // website
                0,    // size
                null, // foundedDate
                null, // description
                null, // logo
                null, // address
                industry,
                TenantStatus.PENDING,
                null  // tenantSettings
        );
    }

    public void addSettings(TenantSettings settings) {
        if(this.tenantSettings != null) {
            throw new TenantStateException("Settings already exists");
        }
        if(settings == null) {
            throw new TenantValidationException("Settings cannot be null!");
        }
        this.tenantSettings = settings;
    }

    public void assignKeycloakGroup(UUID groupId) {
        if (groupId == null) {
            throw new TenantValidationException("Keycloak groupId cannot be null");
        }
        if (this.keycloakGroupId != null) {
            throw new TenantStateException("Keycloak group already assigned");
        }
        this.keycloakGroupId = groupId;
    }

    public void activate() {
        if (this.status != TenantStatus.PENDING && this.status != TenantStatus.SUSPENDED) {
            throw new TenantStateException("Tenant cannot be activated from state: " + status);
        }
        this.status = TenantStatus.ACTIVE;
    }

    public void suspend() {
        if (this.status != TenantStatus.ACTIVE) {
            throw new TenantStateException("Only active tenants can be suspended");
        }
        this.status = TenantStatus.SUSPENDED;
    }

    public void delete() {
        if (this.status == TenantStatus.DELETED) {
            throw new TenantStateException("Tenant already deleted");
        }
        this.status = TenantStatus.DELETED;
    }

    public void updateProfile(
            String name,
            String website,
            String description,
            String logo,
            Address address,
            int size,
            LocalDate foundedDate
    ) {
        if (name == null || name.isBlank()) {
            throw new TenantValidationException("Name cannot be empty");
        }

        this.name = name;
        this.website = website;
        this.description = description;
        this.logo = logo;
        this.address = address;
        this.size = size;
        this.foundedDate = foundedDate;
    }

    private void validate(String name, String email, TenantIndustry industry) {
        if (name == null || name.isBlank()) {
            throw new TenantValidationException("Tenant name is required");
        }

        if (email == null || !email.contains("@")) {
            throw new TenantValidationException("Invalid email");
        }

        if (industry == null) {
            throw new TenantValidationException("Industry is required");
        }
    }
}