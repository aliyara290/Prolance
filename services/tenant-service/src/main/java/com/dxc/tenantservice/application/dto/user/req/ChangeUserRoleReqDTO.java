package com.dxc.tenantservice.application.dto.user.req;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.dxc.tenantservice.domain.model.valueobject.UserRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeUserRoleReqDTO {

    @NotNull(message = "Role is required")
    private UserRole role;
}
