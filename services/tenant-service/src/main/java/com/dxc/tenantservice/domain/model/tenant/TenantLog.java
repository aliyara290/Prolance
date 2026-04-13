package com.dxc.tenantservice.domain.model.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantLog {
    private UUID id;
    private UUID tenantId;
    private String name;
    private String email;
    private String website;
    private int size;
    private LocalDate foundedDate;
    private String description;
    
    private String action;
    private LocalDateTime timestamp;
}
