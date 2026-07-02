package com.dxc.tenantservice.infrastructure.adapter.out.feign.dto.users;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
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
    private List<KeycloakCredentialRepresentation> credentials;

    @Getter
    @Setter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class KeycloakCredentialRepresentation {
        private String type;
        private String value;
        private Boolean temporary;
    }
}
