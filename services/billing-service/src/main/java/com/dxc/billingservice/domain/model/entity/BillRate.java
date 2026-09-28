package com.dxc.billingservice.domain.model.entity;

import com.dxc.billingservice.domain.exception.ValidationException;
import com.dxc.billingservice.domain.model.valueobject.EducationLevel;
import com.dxc.billingservice.domain.model.valueobject.SeniorityLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class BillRate {

    private final UUID id;
    private final UUID tenantId;
    private final UUID projectId;
    private final UUID userId;

    private SeniorityLevel seniorityLevel;
    private EducationLevel educationLevel;
    private BigDecimal hourlyRate;
    private BigDecimal dailyRate;
    private String currency;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private final UUID createdBy;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BillRate create(
            UUID tenantId,
            UUID projectId,
            UUID userId,
            SeniorityLevel seniorityLevel,
            EducationLevel educationLevel,
            BigDecimal hourlyRate,
            BigDecimal dailyRate,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            UUID createdBy
    ) {
        validateRequired(tenantId, "Tenant ID is required");
        validateRequired(projectId, "Project ID is required");
        validateRequired(userId, "User ID is required");
        validateRequired(seniorityLevel, "Seniority level is required");
        validateRequired(educationLevel, "Education level is required");
        validateRequired(hourlyRate, "Hourly rate is required");
        validateRequired(effectiveFrom, "Effective from date is required");
        validateRequired(createdBy, "Created by is required");

        if (hourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Hourly rate must be positive");
        }
        if (dailyRate != null && dailyRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Daily rate must be positive");
        }
        if (effectiveTo != null && effectiveTo.isBefore(effectiveFrom)) {
            throw new ValidationException("Effective to date cannot be before effective from date");
        }

        BigDecimal computedDailyRate = dailyRate != null ? dailyRate : hourlyRate.multiply(BigDecimal.valueOf(8));

        return BillRate.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .projectId(projectId)
                .userId(userId)
                .seniorityLevel(seniorityLevel)
                .educationLevel(educationLevel)
                .hourlyRate(hourlyRate)
                .dailyRate(computedDailyRate)
                .currency("EUR")
                .effectiveFrom(effectiveFrom)
                .effectiveTo(effectiveTo)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void update(
            SeniorityLevel seniorityLevel,
            EducationLevel educationLevel,
            BigDecimal hourlyRate,
            BigDecimal dailyRate,
            LocalDate effectiveFrom,
            LocalDate effectiveTo
    ) {
        if (seniorityLevel != null) this.seniorityLevel = seniorityLevel;
        if (educationLevel != null) this.educationLevel = educationLevel;
        if (effectiveFrom != null) this.effectiveFrom = effectiveFrom;
        if (effectiveTo != null) this.effectiveTo = effectiveTo;

        if (hourlyRate != null) {
            if (hourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Hourly rate must be positive");
            }
            this.hourlyRate = hourlyRate;
        }
        if (dailyRate != null) {
            if (dailyRate.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Daily rate must be positive");
            }
            this.dailyRate = dailyRate;
        }

        if (this.effectiveTo != null && this.effectiveTo.isBefore(this.effectiveFrom)) {
            throw new ValidationException("Effective to date cannot be before effective from date");
        }

        touch();
    }

    public boolean isActiveOn(LocalDate date) {
        if (date.isBefore(effectiveFrom)) return false;
        return effectiveTo == null || !date.isAfter(effectiveTo);
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    private static void validateRequired(Object value, String message) {
        if (value == null) throw new ValidationException(message);
    }
}
