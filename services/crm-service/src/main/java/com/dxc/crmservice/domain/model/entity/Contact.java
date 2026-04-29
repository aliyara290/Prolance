package com.dxc.crmservice.domain.model.entity;

import com.dxc.crmservice.domain.exception.BusinessRuleViolationException;
import com.dxc.crmservice.domain.exception.ValidationException;
import com.dxc.crmservice.domain.model.valueobject.InfluenceLevel;
import com.dxc.crmservice.domain.model.valueobject.Role;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Contact {

    private final UUID id;
    private final UUID tenantId;
    private final UUID clientId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Role role;
    private InfluenceLevel influenceLevel;
    private boolean primary;
    private LocalDateTime lastContactedAt;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Contact(UUID id,
                    UUID tenantId,
                    UUID clientId,
                    String firstName,
                    String lastName,
                    String email,
                    String phone,
                    Role role,
                    InfluenceLevel influenceLevel,
                    boolean primary,
                    String notes) {

        this.id = id == null ? UUID.randomUUID() : id;
        this.tenantId = requireNonNull(tenantId, "tenantId");
        this.clientId = requireNonNull(clientId, "clientId");

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        this.primary = primary;

        updateProfile(firstName, lastName, email, phone, role, influenceLevel, notes);
    }

    public static Contact create(UUID tenantId,
                                 UUID clientId,
                                 String firstName,
                                 String lastName,
                                 String email,
                                 String phone,
                                 Role role,
                                 InfluenceLevel influenceLevel,
                                 boolean primary,
                                 String notes) {

        return new Contact(
                null,
                tenantId,
                clientId,
                firstName,
                lastName,
                email,
                phone,
                role,
                influenceLevel,
                primary,
                notes
        );
    }

    public static Contact rehydrate(UUID id,
                                    UUID tenantId,
                                    UUID clientId,
                                    String firstName,
                                    String lastName,
                                    String email,
                                    String phone,
                                    Role role,
                                    InfluenceLevel influenceLevel,
                                    boolean primary,
                                    LocalDateTime lastContactedAt,
                                    String notes,
                                    LocalDateTime createdAt,
                                    LocalDateTime updatedAt) {

        Contact contact = new Contact(
                id,
                tenantId,
                clientId,
                firstName,
                lastName,
                email,
                phone,
                role,
                influenceLevel,
                primary,
                notes
        );

        contact.lastContactedAt = lastContactedAt;
        contact.createdAt = requireNonNull(createdAt, "createdAt");
        contact.updatedAt = requireNonNull(updatedAt, "updatedAt");

        return contact;
    }

    public void updateProfile(String firstName,
                              String lastName,
                              String email,
                              String phone,
                              Role role,
                              InfluenceLevel influenceLevel,
                              String notes) {

        this.firstName = validateName(firstName, "First name");
        this.lastName = validateName(lastName, "Last name");

        this.email = validateEmail(email);
        this.phone = phone;

        this.role = requireNonNull(role, "role");
        this.influenceLevel = requireNonNull(influenceLevel, "influenceLevel");

        this.notes = notes;

        touch();
    }

    public void markAsPrimary() {
        this.primary = true;
        touch();
    }

    public void demotePrimary() {
        this.primary = false;
        touch();
    }

    public void updateLastContacted() {
        this.lastContactedAt = LocalDateTime.now();
        touch();
    }

    // rules

    private String validateName(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(field + " cannot be empty");
        }
        return value.trim();
    }

    private String validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new ValidationException("Invalid email");
        }
        return email.trim().toLowerCase();
    }

    private static <T> T requireNonNull(T value, String field) {
        return Objects.requireNonNull(value, field + " cannot be null");
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}