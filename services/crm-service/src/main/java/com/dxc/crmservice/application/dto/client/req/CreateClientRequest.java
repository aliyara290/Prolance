package com.dxc.crmservice.application.dto.client.req;

import com.dxc.crmservice.application.dto.AddressDto;
import com.dxc.crmservice.domain.model.valueobject.ClientType;
import com.dxc.crmservice.domain.model.valueobject.Ownership;
import com.dxc.crmservice.domain.model.valueobject.Source;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(
        @NotBlank(message = "Client name is required")
        @Size(min = 2, max = 200)
        String name,

        @Size(max = 100)
        String industry,

        @Size(max = 255)
        String website,

        @Size(max = 50)
        String phone,

        @Valid
        AddressDto address,

        @NotNull(message = "Client type is required")
        ClientType type,

        @NotNull(message = "Source is required")
        Source source,

        @PositiveOrZero(message = "Annual revenue must be zero or positive")
        Double annualRevenue,

        @Size(max = 50)
        String fax,

        Ownership ownership,

        @Size(max = 20)
        String sicCode,

        @Size(max = 2000)
        String description
) {
}
