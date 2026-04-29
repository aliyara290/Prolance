package com.dxc.crmservice.application.dto.opportunity.res;

import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record OpportunityResponse(
    UUID id,
    UUID clientId,
    String title,
    BigDecimal amount,
    Stage stage,
    Priority priority,
    Double estimatedBudget,
    Double expectedRevenue,
    int probability,
    LocalDate expectedStartDate,
    LocalDate expectedEndDate,
    LocalDateTime lastActivityAt,
    LocalDateTime nextFollowUpAt,
    LocalDateTime closingDate,
    String lostReason,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
