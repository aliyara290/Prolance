package com.dxc.crmservice.application.dto.lead.req;

import com.dxc.crmservice.application.dto.client.req.CreateClientRequest;
import com.dxc.crmservice.application.dto.contact.req.CreateContactRequest;
import com.dxc.crmservice.application.validation.ExactlyOneOf;
import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Source;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
        @NotBlank(message = "Lead title is required") @Size(max = 200) String title,

        @Size(max = 2000) String description,

        @NotNull(message = "Source is required") Source source,

        @NotNull(message = "Priority is required") Priority priority,

        UUID clientId,

        UUID contactId,

        @Valid
        ContactRequestDTO contact,

        @Valid
        CreateClientRequest client,

        @NotNull(message = "Assigned to is required")  UUID assignedTo
) {
}