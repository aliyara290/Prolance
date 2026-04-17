package com.dxc.tenantservice.application.dto.tenant.req;

import com.dxc.tenantservice.domain.model.enums.TenantIndustry;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterTenantReqDTO {
    @NotBlank(message = "Company name is required")
    private String name;
    @NotBlank(message = "Company email is required")
    private String companyEmail;

    private String website;

    @NotBlank(message = "Company Industry is required")
    private TenantIndustry industry;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}