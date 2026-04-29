package com.dxc.crmservice.application.dto.opportunity.req;

import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Stage;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateOpportunityRequest(
        @NotNull(message = "Client ID is required")
        UUID clientId,

        @NotBlank(message = "Opportunity name is required")
        @Size(max = 200)
        String title,

        @Size(max = 1000)
        String description,

        @NotNull(message = "Budget is required")
        @PositiveOrZero(message = "Budget must be zero or positive")
        BigDecimal estimatedBudget,

        Double expectedRevenue,

        @Min(value = 0, message = "Probability must be zero or positive")
        @Max(value = 100, message = "Probability must be zero or positive")
        int probability,

        @NotNull(message = "Stage is required")
        Stage stage,

        @FutureOrPresent(message = "Expected start date must be in the future or present")
        LocalDate expectedStartDate,

        @Future(message = "Expected end date must be in the future")
        LocalDate expectedEndDate,

        @NotNull(message = "Priority is required")
        Priority priority

) {
}