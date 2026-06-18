package com.dxc.crmservice.application.dto.lead.req;

import com.dxc.crmservice.application.dto.AddressDto;
import com.dxc.crmservice.domain.model.valueobject.InfluenceLevel;
import com.dxc.crmservice.domain.model.valueobject.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record ContactRequestDTO(
        @NotBlank(message = "First name is required")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100)
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 255)
        String email,

        @Size(max = 50)
        String phone,

        Role role,

        @NotNull(message = "Influence level is required")
        InfluenceLevel influenceLevel,

        boolean primary,

        String notes,

        @Size(max = 100)
        String department,

        LocalDate dateOfBirth,

        @Email(message = "Invalid secondary email format")
        @Size(max = 255)
        String secondaryEmail,

        @Valid
        AddressDto address,

        @Size(max = 2000)
        String description
) {
}
