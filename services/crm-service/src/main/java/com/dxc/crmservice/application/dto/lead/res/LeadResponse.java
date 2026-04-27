package com.dxc.crmservice.application.dto.lead.res;

import com.dxc.crmservice.domain.model.valueobject.LeadStatus;
import com.dxc.crmservice.domain.model.valueobject.LeadPriority;
import com.dxc.crmservice.domain.model.valueobject.Source;

import java.time.LocalDateTime;
import java.util.UUID;

public record LeadResponse(
        UUID id,
        String title,
        String description,
        Source source,
        LeadPriority priority,
        LeadStatus status,
        UUID clientId,
        UUID contactId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
