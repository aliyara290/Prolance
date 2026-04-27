package com.dxc.crmservice.application.dto.contact.req;

import com.dxc.crmservice.domain.model.valueobject.InfluenceLevel;
import com.dxc.crmservice.domain.model.valueobject.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateContactRequest(
    @Size(max = 100)
    String firstName,

    @Size(max = 100)
    String lastName,

    @Email(message = "Invalid email format")
    @Size(max = 255)
    String email,

    @Size(max = 50)
    String phone,

    Role role,

    InfluenceLevel influenceLevel,

    boolean primary,

    String notes,

    UUID clientId
) {}
