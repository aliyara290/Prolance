package com.dxc.crmservice.domain.model.aggregate;

import com.dxc.crmservice.domain.exception.BusinessRuleViolationException;
import com.dxc.crmservice.domain.exception.InvalidStateTransitionException;
import com.dxc.crmservice.domain.exception.ValidationException;
import com.dxc.crmservice.domain.model.valueobject.Address;
import com.dxc.crmservice.domain.model.valueobject.ClientStatus;
import com.dxc.crmservice.domain.model.valueobject.ClientType;
import com.dxc.crmservice.domain.model.valueobject.Ownership;
import com.dxc.crmservice.domain.model.valueobject.Source;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Client {
    private final UUID id;
    private final UUID tenantId;

    private String name;
    private String industry;
    private String website;
    private String phone;
    private Address address;

    private ClientStatus status;
    private final ClientType type;
    private final Source source;

    private Double annualRevenue;
    private String fax;
    private Ownership ownership;
    private String sicCode;
    private String description;

    private UUID createdBy;
    private UUID updatedBy;

    private LocalDateTime deletedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Client(UUID id, UUID tenantId, String name, String industry, String website, String phone, Address address, ClientType type, Source source,
                   Double annualRevenue, String fax, Ownership ownership, String sicCode, String description,
                   UUID createdBy) {

        this.id = id == null ? UUID.randomUUID() : id;
        this.tenantId = requireNonNull(tenantId, "tenantId");

        this.type = requireNonNull(type, "type");
        this.source = requireNonNull(source, "source");

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        this.createdBy = createdBy;
        this.updatedBy = createdBy;

        updateProfile(name, industry, website, phone, address, annualRevenue, fax, ownership, sicCode, description);

        this.status = ClientStatus.ACTIVE;
    }

    public static Client create(UUID tenantId, String name, String industry, String website, String phone, Address address, ClientType type, Source source,
                                Double annualRevenue, String fax, Ownership ownership, String sicCode, String description,
                                UUID createdBy) {

        return new Client(null, tenantId, name, industry, website, phone, address, type, source,
                annualRevenue, fax, ownership, sicCode, description, createdBy);
    }

    public static Client rehydrate(UUID id, UUID tenantId, String name, String industry, String website, String phone, Address address, ClientStatus status, ClientType type, Source source,
                                   Double annualRevenue, String fax, Ownership ownership, String sicCode, String description,
                                   UUID createdBy, UUID updatedBy,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {

        Client client = new Client(id, tenantId, name, industry, website, phone, address, type, source,
                annualRevenue, fax, ownership, sicCode, description, createdBy);

        client.status = requireNonNull(status, "status");
        client.updatedBy = updatedBy;
        client.createdAt = requireNonNull(createdAt, "createdAt");
        client.updatedAt = requireNonNull(updatedAt, "updatedAt");

        return client;
    }

    public void updateProfile(String name, String industry, String website, String phone, Address address,
                              Double annualRevenue, String fax, Ownership ownership, String sicCode, String description) {

        ensureNotArchived();

        this.name = validateName(name);
        this.industry = industry;
        this.website = website;
        this.phone = phone;
        this.address = address;
        this.annualRevenue = annualRevenue;
        this.fax = fax;
        this.ownership = ownership;
        this.sicCode = sicCode;
        this.description = description;

        touch();
    }

    public void activate() {
        ensureNotArchived();
        this.status = ClientStatus.ACTIVE;
        touch();
    }

    public void deactivate() {
        ensureNotArchived();
        this.status = ClientStatus.INACTIVE;
        touch();
    }

    public void markAsArchived() {
        if (this.status == ClientStatus.ARCHIVED) {
            throw new BusinessRuleViolationException("Client already archived");
        }
        this.status = ClientStatus.ARCHIVED;
        touch();
    }


    private void ensureNotArchived() {
        if (this.status == ClientStatus.ARCHIVED) {
            throw new BusinessRuleViolationException("Cannot modify a archived client");
        }
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Client name cannot be empty");
        }
        return name.trim();
    }

    private static <T> T requireNonNull(T value, String field) {
        return Objects.requireNonNull(value, field + " cannot be null");
    }



    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}