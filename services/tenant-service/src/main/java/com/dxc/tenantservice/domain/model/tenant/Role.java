package com.dxc.tenantservice.domain.model.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private UUID id;
    private UUID tenantId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
