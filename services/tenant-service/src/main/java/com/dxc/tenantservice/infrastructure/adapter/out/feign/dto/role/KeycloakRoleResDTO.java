package com.dxc.tenantservice.infrastructure.adapter.out.feign.dto.role;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeycloakRoleResDTO {
    private UUID id;
    private String name;
    private String composite;
    private String clientRole;
    private String containerId;

}
