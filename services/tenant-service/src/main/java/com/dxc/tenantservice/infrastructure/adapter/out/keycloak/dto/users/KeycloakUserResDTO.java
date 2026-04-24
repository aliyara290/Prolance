package com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.users;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeycloakUserResDTO {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String enabled;
    private Map<String, List<String>> attributes;
}