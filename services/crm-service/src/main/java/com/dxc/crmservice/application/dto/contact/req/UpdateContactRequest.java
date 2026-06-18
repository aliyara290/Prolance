package com.dxc.crmservice.application.dto.contact.req;

import com.dxc.crmservice.application.dto.AddressDto;
import com.dxc.crmservice.domain.model.valueobject.InfluenceLevel;
import com.dxc.crmservice.domain.model.valueobject.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
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

    UUID clientId,

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
) {}
