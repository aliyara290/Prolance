package com.dxc.crmservice.application.dto.lead.req;

import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.Source;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateLeadRequest(
        @NotBlank(message = "Lead title is required") @Size(max = 200) String title,

        @Size(max = 2000) String description,

        @NotNull(message = "Source is required") Source source,

        @NotNull(message = "Priority is required") Priority priority,

        UUID clientId,

        UUID contactId) {
}
