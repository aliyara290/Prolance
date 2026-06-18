package com.dxc.crmservice.application.dto.lead.req;

import com.dxc.crmservice.application.dto.AddressDto;
import com.dxc.crmservice.application.dto.client.req.CreateClientRequest;
import com.dxc.crmservice.application.dto.contact.req.CreateContactRequest;
import com.dxc.crmservice.application.validation.ExactlyOneOf;
import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Source;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.UUID;

@ExactlyOneOf(
        fields = {"clientId", "client"},
        message = "Provide either clientId or client, but not both"
)
@ExactlyOneOf(
        fields = {"contactId", "contact"},
        message = "Provide either contactId or contact, but not both"
)
public record CreateLeadRequest(
        @NotBlank(message = "Lead title is required")
        @Size(max = 200)
        String title,

        @Size(max = 2000)
        String description,

        @NotNull(message = "Source is required")
        Source source,

        @NotNull(message = "Priority is required")
        Priority priority,

        UUID clientId,

        UUID contactId,

        @Valid
        ContactRequestDTO contact,

        @Valid
        CreateClientRequest client,

        @NotNull(message = "Assigned to is required")
        UUID assignedTo,

        @Size(max = 50) String phone,

        @Size(max = 100) String industry,

        @PositiveOrZero(message = "Annual revenue must be zero or positive")
        Double annualRevenue,

        @Size(max = 200)
        String company,

        @Email(message = "Invalid email format")
        @Size(max = 255)
        String email,

        @Size(max = 255)
        String website,

        @PositiveOrZero(message = "Number of employees must be zero or positive")
        Integer numberOfEmployees,

        @Valid
        AddressDto address
) {
}