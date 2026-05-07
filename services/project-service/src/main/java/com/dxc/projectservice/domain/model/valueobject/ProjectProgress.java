package com.dxc.projectservice.domain.model.valueobject;

import com.dxc.projectservice.domain.exception.ValidationException;

public record ProjectProgress(float percentage) {
    public ProjectProgress {
        if (percentage < 0 || percentage > 100) {
            throw new ValidationException("Progress percentage must be between 0 and 100");
        }
    }

    public static ProjectProgress zero() {
        return new ProjectProgress(0);
    }

    public static ProjectProgress complete() {
        return new ProjectProgress(100);
    }

    public boolean isComplete() {
        return percentage >= 100;
    }
}
