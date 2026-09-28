package com.dxc.billingservice.application.dto.req;

import com.dxc.billingservice.domain.model.valueobject.EducationLevel;
import com.dxc.billingservice.domain.model.valueobject.SeniorityLevel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateBillRateRequest(
        SeniorityLevel seniorityLevel,
        EducationLevel educationLevel,
        BigDecimal hourlyRate,
        BigDecimal dailyRate,
        LocalDate effectiveFrom,
        LocalDate effectiveTo
) {}
