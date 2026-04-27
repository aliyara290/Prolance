package com.dxc.crmservice.application.dto.opportunity.req;

import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Stage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateOpportunityRequest(@NotBlank(message = "Opportunity name is required") @Size(max = 200) String name,

                                       @PositiveOrZero(message = "Amount must be zero or positive") BigDecimal amount,

                                       @NotNull(message = "Stage is required") Stage stage,

                                       @NotNull(message = "Priority is required") Priority priority,

                                       LocalDateTime probabilityDate,

                                       @NotNull(message = "Client ID is required") UUID clientId) {
}
