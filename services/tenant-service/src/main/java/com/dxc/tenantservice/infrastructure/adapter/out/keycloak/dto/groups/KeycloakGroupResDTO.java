package com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.groups;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class KeycloakGroupResDTO {
    private UUID id;
    private String name;
    private String path;
    private List<KeycloakGroupResDTO> children;
}
