package com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.role;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class KeycloakRoleReqDTO {
    private UUID id;
    private String name;
}
