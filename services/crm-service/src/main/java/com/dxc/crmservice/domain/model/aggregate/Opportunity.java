package com.dxc.crmservice.domain.model.aggregate;

import com.dxc.crmservice.domain.exception.BusinessRuleViolationException;
import com.dxc.crmservice.domain.exception.InvalidStateTransitionException;
import com.dxc.crmservice.domain.exception.ValidationException;
import com.dxc.crmservice.domain.model.valueobject.OpportunityType;
import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Source;
import com.dxc.crmservice.domain.model.valueobject.Stage;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Opportunity {

    private final UUID id;
    private final UUID tenantId;
    private final UUID clientId;

    private String title;
    private String description;

    private Double estimatedBudget;
    private Double expectedRevenue;

    private int probability;

    private LocalDate expectedStartDate;
    private LocalDate expectedEndDate;

    private LocalDateTime closingDate;

    private Stage stage;

    private LocalDateTime lastActivityAt;
    private LocalDateTime nextFollowUpAt;

    private Priority priority;
    private String lostReason;

    private OpportunityType type;
    private Source source;

    private UUID createdBy;
    private UUID updatedBy;

    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private Opportunity(UUID id, UUID tenantId, UUID clientId, String title, String description, Double estimatedBudget, Double expectedRevenue, int probability, Stage stage, Priority priority,
                        OpportunityType type, Source source, UUID createdBy) {

        this.id = id == null ? UUID.randomUUID() : id;
        this.tenantId = requireNonNull(tenantId, "tenantId");
        this.clientId = requireNonNull(clientId, "clientId");

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        this.stage = requireNonNull(stage, "stage");
        this.priority = priority;

        this.type = type;
        this.source = source;

        this.createdBy = createdBy;
        this.updatedBy = createdBy;

        updateDetails(title, description, estimatedBudget, expectedRevenue, probability, null, null);
    }

    // factory
    public static Opportunity create(UUID tenantId, UUID clientId, String title, String description, Double estimatedBudget, Double expectedRevenue, int probability, Stage stage, Priority priority,
                                     OpportunityType type, Source source, UUID createdBy) {

        return new Opportunity(null, tenantId, clientId, title, description, estimatedBudget, expectedRevenue, probability, stage, priority,
                type, source, createdBy);
    }

    public static Opportunity rehydrate(UUID id, UUID tenantId, UUID clientId, String title, String description, Double estimatedBudget, Double expectedRevenue, int probability, Priority priority, LocalDate expectedStartDate, LocalDate expectedEndDate, LocalDateTime closingDate, Stage stage, LocalDateTime lastActivityAt, LocalDateTime nextFollowUpAt, String lostReason,
                                        OpportunityType type, Source source,
                                        UUID createdBy, UUID updatedBy,
                                        LocalDateTime createdAt, LocalDateTime updatedAt) {

        Opportunity opp = new Opportunity(id, tenantId, clientId, title, description, estimatedBudget, expectedRevenue, probability, stage, priority,
                type, source, createdBy);

        opp.expectedStartDate = expectedStartDate;
        opp.expectedEndDate = expectedEndDate;
        opp.closingDate = closingDate;

        opp.lastActivityAt = lastActivityAt;
        opp.nextFollowUpAt = nextFollowUpAt;
        opp.lostReason = lostReason;

        opp.updatedBy = updatedBy;
        opp.createdAt = requireNonNull(createdAt, "createdAt");
        opp.updatedAt = requireNonNull(updatedAt, "updatedAt");

        return opp;
    }

    public void updateDetails(String title, String description, Double estimatedBudget, Double expectedRevenue, int probability, LocalDate expectedStartDate, LocalDate expectedEndDate) {

        ensureNotClosed();

        this.title = validateTitle(title);
        this.description = description;

        this.estimatedBudget = validateMoney(estimatedBudget);
        this.expectedRevenue = validateMoney(expectedRevenue);

        this.probability = validateProbability(probability);

        validateDates(expectedStartDate, expectedEndDate);
        this.expectedStartDate = expectedStartDate;
        this.expectedEndDate = expectedEndDate;

        touch();
    }

    //    behaviors
    public void moveStage(Stage newStage) {
        ensureNotClosed();

        if (newStage == null) {
            throw new ValidationException("Stage cannot be null");
        }

//        if (!this.stage.canMoveTo(newStage)) {
//            throw new InvalidStateTransitionException("Invalid stage transition: " + this.stage + " → " + newStage);
//        }

        this.stage = newStage;

        if (newStage.isClosed()) {
            this.closingDate = LocalDateTime.now();
        }

        touch();
    }

    public void markAsLost(String reason) {
        ensureNotClosed();

        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("Lost reason is required");
        }

        this.stage = Stage.LOST;
        this.lostReason = reason;
        this.closingDate = LocalDateTime.now();

        touch();
    }

    public void markAsWon() {
        ensureNotClosed();

        this.stage = Stage.WON;
        this.closingDate = LocalDateTime.now();

        touch();
    }

    public void scheduleFollowUp(LocalDateTime nextFollowUpAt) {
        ensureNotClosed();

        if (nextFollowUpAt == null || nextFollowUpAt.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Follow-up must be in the future");
        }

        this.nextFollowUpAt = nextFollowUpAt;
        touch();
    }

    public void recordActivity() {
        this.lastActivityAt = LocalDateTime.now();
        touch();
    }

    //    roles
    private void ensureNotClosed() {
        if (this.stage.isClosed()) {
            throw new BusinessRuleViolationException("Cannot modify a closed opportunity");
        }
    }

    private String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Title cannot be empty");
        }
        return title.trim();
    }

    private Double validateMoney(Double value) {
        if (value != null && value < 0) {
            throw new ValidationException("Money value cannot be negative");
        }
        return value;
    }

    private int validateProbability(int value) {
        if (value < 0 || value > 100) {
            throw new BusinessRuleViolationException("Probability must be 0-100");
        }
        return value;
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new BusinessRuleViolationException("End date cannot be before start date");
        }
    }

    private static <T> T requireNonNull(T value, String field) {
        return Objects.requireNonNull(value, field + " cannot be null");
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}