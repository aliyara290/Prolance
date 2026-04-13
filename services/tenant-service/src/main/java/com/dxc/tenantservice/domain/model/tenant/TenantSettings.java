package com.dxc.tenantservice.domain.model.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSettings {
    private UUID id;
    private UUID tenantId;
    private String logo;

    private String primaryColor;
}
