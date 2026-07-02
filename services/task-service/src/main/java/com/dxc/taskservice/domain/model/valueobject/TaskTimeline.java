package com.dxc.taskservice.domain.model.valueobject;

import com.dxc.taskservice.domain.exception.ValidationException;

import java.time.LocalDateTime;

public record TaskTimeline(
        LocalDateTime startDate,
        LocalDateTime dueDate,
        LocalDateTime completedAt
) {
    public TaskTimeline {
        if (startDate == null) {
            throw new ValidationException("Start date is required");
        }

        if (dueDate != null && dueDate.isBefore(startDate)) {
            throw new ValidationException("Due date cannot be before start date");
        }

        if (completedAt != null && completedAt.isBefore(startDate)) {
            throw new ValidationException("Completed date cannot be before start date");
        }
    }

    public static TaskTimeline of(LocalDateTime startDate, LocalDateTime dueDate) {
        return new TaskTimeline(startDate, dueDate, null);
    }

    public TaskTimeline complete(LocalDateTime completedAt) {
        return new TaskTimeline(this.startDate, this.dueDate, completedAt);
    }

    public TaskTimeline updateDates(LocalDateTime newStartDate, LocalDateTime newDueDate) {
        return new TaskTimeline(
                newStartDate != null ? newStartDate : this.startDate,
                newDueDate != null ? newDueDate : this.dueDate,
                this.completedAt
        );
    }

    public boolean isOverdue() {
        return dueDate != null && completedAt == null && LocalDateTime.now().isAfter(dueDate);
    }
}
