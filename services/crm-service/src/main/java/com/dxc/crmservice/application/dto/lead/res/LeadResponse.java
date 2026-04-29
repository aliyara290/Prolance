package com.dxc.crmservice.application.dto.lead.res;

import com.dxc.crmservice.application.dto.client.res.ClientResponse;
import com.dxc.crmservice.application.dto.contact.res.ContactResponse;
import com.dxc.crmservice.domain.model.valueobject.LeadStatus;
import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Source;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

public record LeadResponse(
        UUID id,
        String title,
        String description,
        Source source,
        Priority priority,
        LeadStatus status,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        UUID clientId,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        UUID contactId,
        UUID assignedTo,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        ContactResponse contact,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        ClientResponse client,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
