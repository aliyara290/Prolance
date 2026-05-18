package com.dxc.crmservice.application.dto.opportunity.req;

import com.dxc.crmservice.domain.model.valueobject.OpportunityType;
import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Source;
import com.dxc.crmservice.domain.model.valueobject.Stage;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateOpportunityRequest(
    @Size(max = 200)
    String name,

    @PositiveOrZero(message = "Amount must be zero or positive")
    BigDecimal amount,

    Stage stage,

    Priority priority,

    LocalDateTime closingDate,

    UUID clientId,

    OpportunityType type,

    Source source
) {}
