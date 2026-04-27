package com.dxc.crmservice.application.dto.lead.req;

import com.dxc.crmservice.domain.model.valueobject.Priority;
import com.dxc.crmservice.domain.model.valueobject.LeadStatus;
import com.dxc.crmservice.domain.model.valueobject.Source;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CreateLeadReqDTO {
    private UUID clientId;

    private UUID contactId;

    @NotBlank(message = "title must not be empty")
    @Size(max = 150, message = "title must not exceed 150 characters")
    private String title;

    @Size(max = 1000, message = "description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "source is required")
    private Source source;

    @NotNull(message = "status is required")
    private LeadStatus status;

    @Min(value = 0, message = "score must be at least 0")
    @Max(value = 100, message = "score must not exceed 100")
    private int score;

    @NotNull(message = "priority is required")
    private Priority priority;

    @NotNull(message = "assignedTo is required")
    private UUID assignedTo;
}