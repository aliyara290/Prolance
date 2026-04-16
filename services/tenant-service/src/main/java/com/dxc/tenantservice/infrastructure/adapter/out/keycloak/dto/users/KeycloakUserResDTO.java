package com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.users;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class KeycloakUserResDTO {
    private UUID id;
    private UUID tenantId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String enabled;
}
