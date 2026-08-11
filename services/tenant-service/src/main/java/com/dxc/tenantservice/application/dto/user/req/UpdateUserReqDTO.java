package com.dxc.tenantservice.application.dto.user.req;

import com.dxc.tenantservice.domain.model.valueobject.Language;
import com.dxc.tenantservice.domain.model.valueobject.Theme;
import com.dxc.tenantservice.domain.model.valueobject.SeniorityLevel;
import com.dxc.tenantservice.domain.model.valueobject.EducationLevel;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserReqDTO {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Size(max = 100, message = "Job title must not exceed 100 characters")
    private String jobTitle;

    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;

    private SeniorityLevel seniorityLevel;
    private EducationLevel educationLevel;

    @PositiveOrZero(message = "Base hourly salary must be zero or positive")
    private BigDecimal baseHourlySalary;
}
