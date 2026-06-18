package com.dxc.crmservice.application.dto.lead.req;

import com.dxc.crmservice.application.dto.AddressDto;
import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Source;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateLeadRequest(
        @Size(max = 200) String title,

        @Size(max = 2000) String description,

        Source source,

        Priority priority,

        UUID clientId,

        UUID contactId,

        @Size(max = 50) String phone,

        @Size(max = 100) String industry,

        @PositiveOrZero(message = "Annual revenue must be zero or positive")
        Double annualRevenue,

        @Size(max = 200) String company,

        @Email(message = "Invalid email format")
        @Size(max = 255) String email,

        @Size(max = 255) String website,

        @PositiveOrZero(message = "Number of employees must be zero or positive")
        Integer numberOfEmployees,

        @Valid AddressDto address) {
}
