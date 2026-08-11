package com.dxc.billingservice.application.dto.req;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record GenerateInvoiceRequest(
        @NotNull UUID projectId,
        @NotNull UUID clientId,
        @NotNull LocalDate periodStartDate,
        @NotNull LocalDate periodEndDate,
        @NotNull LocalDate dueDate,
        BigDecimal taxRate,
        String notes
) {}
