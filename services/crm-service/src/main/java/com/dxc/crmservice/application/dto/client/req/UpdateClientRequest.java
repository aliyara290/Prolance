package com.dxc.crmservice.application.dto.client.req;

import com.dxc.crmservice.application.dto.AddressDto;
import com.dxc.crmservice.domain.model.valueobject.ClientType;
import com.dxc.crmservice.domain.model.valueobject.Source;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(
        @Size(min = 2, max = 200) String name,

        @Size(max = 100) String industry,

        @Size(max = 255) String website,

        @Size(max = 50) String phone,

        @Valid AddressDto address,

        @Size(max = 100) String country,

        ClientType type,

        Source source) {
}
