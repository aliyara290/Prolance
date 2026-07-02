package com.dxc.tenantservice.infrastructure.adapter.out.feign.dto.groups;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KeycloakGroupResDTO {
    private UUID id;
    private String name;
    private String path;
    private List<KeycloakGroupResDTO> children;
}
