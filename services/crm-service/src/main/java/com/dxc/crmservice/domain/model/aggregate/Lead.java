package com.dxc.crmservice.domain.model.aggregate;

import com.dxc.crmservice.domain.exception.BusinessRuleViolationException;
import com.dxc.crmservice.domain.exception.InvalidStateTransitionException;
import com.dxc.crmservice.domain.exception.ValidationException;
import com.dxc.crmservice.domain.model.valueobject.Address;
import com.dxc.crmservice.domain.model.valueobject.LeadStatus;
import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Source;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Lead {
    private final UUID id;
    private final UUID tenantId;

    private UUID clientId;
    private UUID contactId;

    private String title;
    private String description;

    private Source source;
    private LeadStatus status;
    private int score;
    private Priority priority;

    private UUID assignedTo;

    private LocalDateTime firstContactedAt;
    private LocalDateTime lastActivityAt;
    private String unqualifiedReason;

    private String phone;
    private String industry;
    private Double annualRevenue;
    private String company;
    private String email;
    private String website;
    private Integer numberOfEmployees;
    private Address address;

    private UUID createdBy;
    private UUID updatedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Lead(UUID id,
            UUID tenantId,
            String title,
            String description,
            Source source,
            Priority priority,
            String phone,
            String industry,
            Double annualRevenue,
            String company,
            String email,
            String website,
            Integer numberOfEmployees,
            Address address,
            UUID createdBy
    ) {

        this.id = id == null ? UUID.randomUUID() : id;
        this.tenantId = requireNonNull(tenantId, "tenantId");

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        this.status = LeadStatus.NEW;
        this.score = 0;

        this.createdBy = createdBy;
        this.updatedBy = createdBy;

        updateDetails(title, description, source, priority, phone, industry, annualRevenue, company, email, website, numberOfEmployees, address);
    }

    public static Lead create(UUID tenantId,
            String title,
            String description,
            Source source,
            Priority priority,
            String phone,
            String industry,
            Double annualRevenue,
            String company,
            String email,
            String website,
            Integer numberOfEmployees,
            Address address,
            UUID createdBy
    ) {

        return new Lead(null, tenantId, title, description, source, priority,
                phone, industry, annualRevenue, company, email, website, numberOfEmployees, address, createdBy);
    }

    public static Lead rehydrate(
            UUID id,
            UUID tenantId,
            UUID clientId,
            UUID contactId,
            String title,
            String description,
            Source source,
            LeadStatus status,
            int score,
            Priority priority,
            UUID assignedTo,
            LocalDateTime firstContactedAt,
            LocalDateTime lastActivityAt,
            String unqualifiedReason,
            String phone,
            String industry,
            Double annualRevenue,
            String company,
            String email,
            String website,
            Integer numberOfEmployees,
            Address address,
            UUID createdBy,
            UUID updatedBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        Lead lead = new Lead(id, tenantId, title, description, source, priority,
                phone, industry, annualRevenue, company, email, website, numberOfEmployees, address, createdBy);

        lead.clientId = clientId;
        lead.contactId = contactId;
        lead.status = requireNonNull(status, "status");
        lead.score = score;
        lead.assignedTo = assignedTo;
        lead.firstContactedAt = firstContactedAt;
        lead.lastActivityAt = lastActivityAt;
        lead.unqualifiedReason = unqualifiedReason;
        lead.updatedBy = updatedBy;
        lead.createdAt = requireNonNull(createdAt, "createdAt");
        lead.updatedAt = requireNonNull(updatedAt, "updatedAt");

        return lead;
    }

    public void updateDetails(String title,
            String description,
            Source source,
            Priority priority,
            String phone,
            String industry,
            Double annualRevenue,
            String company,
            String email,
            String website,
            Integer numberOfEmployees,
            Address address) {

        ensureNotClosed();

        this.title = validateTitle(title);
        this.description = description;
        this.source = source;
        this.priority = priority;
        this.phone = phone;
        this.industry = industry;
        this.annualRevenue = annualRevenue;
        this.company = company;
        this.email = email;
        this.website = website;
        this.numberOfEmployees = numberOfEmployees;
        this.address = address;

        touch();
    }

    public void updateScore(int newScore) {
        ensureNotClosed();

        if (newScore < 0) {
            throw new ValidationException("Score cannot be negative");
        }

        this.score = newScore;
        touch();
    }

    public void assignTo(UUID userId) {
        ensureNotClosed();

        this.assignedTo = requireNonNull(userId, "assignedTo");
        touch();
    }

    public void markAsContacted() {
        ensureStatus(LeadStatus.NEW);

        this.status = LeadStatus.CONTACTED;
        this.firstContactedAt = LocalDateTime.now();
        this.lastActivityAt = this.firstContactedAt;

        touch();
    }

    public void qualify() {
        ensureStatus(LeadStatus.CONTACTED);

        this.status = LeadStatus.QUALIFIED;
        touch();
    }

    public void markAsUnqualified(String reason) {
        ensureNotClosed();

        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("Unqualified reason is required");
        }

        this.status = LeadStatus.UNQUALIFIED;
        this.unqualifiedReason = reason;
        touch();
    }

    public void assignClient(UUID clientId) {
        ensureNotClosed();

        this.clientId = requireNonNull(clientId, "clientId");
        touch();
    }

    public void addContact(UUID contactId) {
        ensureNotClosed();
        this.contactId = requireNonNull(contactId, "contactId");
        touch();
    }

    public void createdBy(UUID createdBy) {
        this.createdBy = requireNonNull(createdBy, "createdBy");
    }


    private void ensureNotClosed() {
        if (status == LeadStatus.QUALIFIED || status == LeadStatus.UNQUALIFIED) {
            throw new BusinessRuleViolationException("Cannot modify a closed lead");
        }
    }

    private void ensureStatus(LeadStatus expected) {
        if (this.status != expected) {
            throw new InvalidStateTransitionException(
                    "Invalid status transition: expected" + expected + " but was " + status);
        }
    }

    private String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Lead title cannot be empty");
        }
        return title.trim();
    }

    private static <T> T requireNonNull(T value, String field) {
        return Objects.requireNonNull(value, field + " cannot be null");
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

}