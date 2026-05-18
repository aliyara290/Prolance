package com.dxc.crmservice.domain.model.entity;

import com.dxc.crmservice.domain.exception.BusinessRuleViolationException;
import com.dxc.crmservice.domain.exception.ValidationException;
import com.dxc.crmservice.domain.model.valueobject.Address;
import com.dxc.crmservice.domain.model.valueobject.InfluenceLevel;
import com.dxc.crmservice.domain.model.valueobject.Role;
import lombok.Getter;

import java.time.LocalDate;
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

    private String department;
    private LocalDate dateOfBirth;
    private String secondaryEmail;
    private Address address;
    private String description;

    private UUID createdBy;
    private UUID updatedBy;

    private LocalDateTime deletedAt;
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
                    String notes,
                    String department,
                    LocalDate dateOfBirth,
                    String secondaryEmail,
                    Address address,
                    String description,
                    UUID createdBy) {

        this.id = id == null ? UUID.randomUUID() : id;
        this.tenantId = requireNonNull(tenantId, "tenantId");
        this.clientId = requireNonNull(clientId, "clientId");

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        this.primary = primary;

        this.createdBy = createdBy;
        this.updatedBy = createdBy;

        updateProfile(firstName, lastName, email, phone, role, influenceLevel, notes,
                department, dateOfBirth, secondaryEmail, address, description);
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
                                 String notes,
                                 String department,
                                 LocalDate dateOfBirth,
                                 String secondaryEmail,
                                 Address address,
                                 String description,
                                 UUID createdBy) {

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
                notes,
                department,
                dateOfBirth,
                secondaryEmail,
                address,
                description,
                createdBy
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
                                    String department,
                                    LocalDate dateOfBirth,
                                    String secondaryEmail,
                                    Address address,
                                    String description,
                                    UUID createdBy,
                                    UUID updatedBy,
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
                notes,
                department,
                dateOfBirth,
                secondaryEmail,
                address,
                description,
                createdBy
        );

        contact.lastContactedAt = lastContactedAt;
        contact.updatedBy = updatedBy;
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
                              String notes,
                              String department,
                              LocalDate dateOfBirth,
                              String secondaryEmail,
                              Address address,
                              String description) {

        this.firstName = validateName(firstName, "First name");
        this.lastName = validateName(lastName, "Last name");

        this.email = validateEmail(email);
        this.phone = phone;

        this.role = requireNonNull(role, "role");
        this.influenceLevel = requireNonNull(influenceLevel, "influenceLevel");

        this.notes = notes;
        this.department = department;
        this.dateOfBirth = dateOfBirth;
        this.secondaryEmail = secondaryEmail;
        this.address = address;
        this.description = description;

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