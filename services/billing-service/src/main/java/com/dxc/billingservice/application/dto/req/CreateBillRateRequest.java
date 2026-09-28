package com.dxc.billingservice.application.dto.req;

import com.dxc.billingservice.domain.model.valueobject.EducationLevel;
import com.dxc.billingservice.domain.model.valueobject.SeniorityLevel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBillRateRequest(
        @NotNull UUID projectId,
        @NotNull UUID userId,
        @NotNull SeniorityLevel seniorityLevel,
        @NotNull EducationLevel educationLevel,
        @NotNull @Positive BigDecimal hourlyRate,
        BigDecimal dailyRate,
        @NotNull LocalDate effectiveFrom,
        LocalDate effectiveTo
) {}
