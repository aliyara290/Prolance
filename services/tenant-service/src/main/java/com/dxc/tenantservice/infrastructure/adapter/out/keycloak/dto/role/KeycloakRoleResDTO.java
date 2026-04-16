package com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.role;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class KeycloakRoleResDTO {
    private UUID id;
    private String name;
    private String composite;
    private String clientRole;
    private String containerId;

}
