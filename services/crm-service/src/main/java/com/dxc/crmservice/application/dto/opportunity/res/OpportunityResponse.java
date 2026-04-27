package com.dxc.crmservice.application.dto.opportunity.res;

import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OpportunityResponse(
    UUID id,
    String name,
    BigDecimal amount,
    Stage stage,
    Priority priority,
    LocalDateTime closingDate,
    UUID clientId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
