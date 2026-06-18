package com.dxc.projectservice.domain.model.valueobject;

import com.dxc.projectservice.domain.exception.ValidationException;
import java.time.LocalDateTime;

public record ProjectTimeline(
        LocalDateTime plannedStartDate,
        LocalDateTime plannedEndDate,
        LocalDateTime actualStartDate,
        LocalDateTime actualEndDate
) {
    public ProjectTimeline {
        if (plannedStartDate == null) {
            throw new ValidationException("Planned start date is required");
        }

        if (plannedEndDate != null && plannedEndDate.isBefore(plannedStartDate)) {
            throw new ValidationException("Planned end date cannot be before planned start date");
        }

        if (actualEndDate != null && actualStartDate == null) {
            throw new ValidationException("Actual start date is required when actual end date exists");
        }

        if (actualEndDate != null && actualEndDate.isBefore(actualStartDate)) {
            throw new ValidationException("Actual end date cannot be before actual start date");
        }
    }

    public ProjectTimeline start(LocalDateTime startDate) {
        return new ProjectTimeline(plannedStartDate, plannedEndDate, startDate, null);
    }

    public ProjectTimeline finish(LocalDateTime endDate) {
        return new ProjectTimeline(plannedStartDate, plannedEndDate, actualStartDate, endDate);
    }
}