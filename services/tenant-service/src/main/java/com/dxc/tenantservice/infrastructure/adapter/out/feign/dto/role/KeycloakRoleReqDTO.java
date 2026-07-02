package com.dxc.tenantservice.infrastructure.adapter.out.feign.dto.role;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeycloakRoleReqDTO {
    private UUID id;
    private String name;
}
