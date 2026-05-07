package com.dxc.projectservice.domain.model.valueobject;

import com.dxc.projectservice.domain.exception.ValidationException;
import java.math.BigDecimal;
import java.util.Objects;

public record ProjectFinancials(
        BigDecimal estimatedBudget,
        BigDecimal actualCost
) {
    public ProjectFinancials {
        Objects.requireNonNull(estimatedBudget, "Estimated budget cannot be null");
        Objects.requireNonNull(actualCost, "Actual cost cannot be null");

        if (estimatedBudget.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Estimated budget cannot be negative");
        }

        if (actualCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Actual cost cannot be negative");
        }
    }

    public static ProjectFinancials empty() {
        return new ProjectFinancials(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public ProjectFinancials updateActualCost(BigDecimal newActualCost) {
        return new ProjectFinancials(this.estimatedBudget, newActualCost);
    }

    public ProjectFinancials updateBudget(BigDecimal newBudget) {
        return new ProjectFinancials(newBudget, this.actualCost);
    }

    public BigDecimal getVariance() {
        return estimatedBudget.subtract(actualCost);
    }
}