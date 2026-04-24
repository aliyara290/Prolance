package com.dxc.tenantservice.infrastructure.adapter.out.keycloak.dto.groups;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateKeycloakGroupReqDTO {
    private String name;
}