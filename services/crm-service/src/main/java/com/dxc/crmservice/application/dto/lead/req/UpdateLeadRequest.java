package com.dxc.crmservice.application.dto.lead.req;

import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Source;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateLeadRequest(
        @Size(max = 200) String title,

        @Size(max = 2000) String description,

        Source source,

        Priority priority,

        UUID clientId,

        UUID contactId) {
}
