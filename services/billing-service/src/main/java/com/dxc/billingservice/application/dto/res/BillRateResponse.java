package com.dxc.billingservice.application.dto.res;

import com.dxc.billingservice.domain.model.valueobject.EducationLevel;
import com.dxc.billingservice.domain.model.valueobject.SeniorityLevel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BillRateResponse(
        UUID id,
        UUID projectId,
        UUID userId,
        SeniorityLevel seniorityLevel,
        EducationLevel educationLevel,
        BigDecimal hourlyRate,
        BigDecimal dailyRate,
        String currency,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        UUID createdBy,
        LocalDateTime createdAt
) {}
