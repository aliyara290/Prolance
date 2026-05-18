package com.dxc.crmservice.application.dto.client.res;

import com.dxc.crmservice.application.dto.AddressDto;
import com.dxc.crmservice.domain.model.valueobject.ClientStatus;
import com.dxc.crmservice.domain.model.valueobject.ClientType;
import com.dxc.crmservice.domain.model.valueobject.Ownership;
import com.dxc.crmservice.domain.model.valueobject.Source;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        String name,
        String industry,
        String website,
        String phone,
        AddressDto address,
        String country,
        ClientStatus status,
        ClientType type,
        Source source,
        Double annualRevenue,
        String fax,
        Ownership ownership,
        String sicCode,
        String description,
        UUID createdBy,
        UUID updatedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}