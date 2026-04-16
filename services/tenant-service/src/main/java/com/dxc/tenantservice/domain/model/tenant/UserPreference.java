package com.dxc.tenantservice.domain.model.tenant;

import com.dxc.tenantservice.domain.model.enums.Language;
import com.dxc.tenantservice.domain.model.enums.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {
    private UUID id;
    private UUID tenantId;
    private UUID userId;
    private Language language;
    private String timezone;
    private Theme theme;
    private LocalDateTime updatedAt;
}
