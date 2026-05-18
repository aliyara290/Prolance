package com.dxc.crmservice.application.dto.contact.res;

import com.dxc.crmservice.application.dto.AddressDto;
import com.dxc.crmservice.domain.model.valueobject.InfluenceLevel;
import com.dxc.crmservice.domain.model.valueobject.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ContactResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phone,
    Role role,
    InfluenceLevel influenceLevel,
    boolean primary,
    String notes,
    UUID clientId,
    LocalDateTime lastContactedAt,
    String department,
    LocalDate dateOfBirth,
    String secondaryEmail,
    AddressDto address,
    String description,
    UUID createdBy,
    UUID updatedBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
