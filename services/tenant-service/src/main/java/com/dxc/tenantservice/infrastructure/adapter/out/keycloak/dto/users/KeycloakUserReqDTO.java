package com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class KeycloakUserReqDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean enabled;
    private Boolean emailVerified;
    private Map<String, List<String>> attributes;
//    private List<KeycloakCredentialRepresentation> credentials;
}
