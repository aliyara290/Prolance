package com.dxc.tenantservice.infrastructure.adapter.out.feign.dto.groups;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateKeycloakGroupReqDTO {
    private String name;
}