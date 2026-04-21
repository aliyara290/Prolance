package com.dxc.tenantservice.application.dto.tenant.req;

import com.dxc.tenantservice.domain.model.enums.TenantIndustry;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class RegisterTenantReqDTO {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Company email is required")
    @Email(message = "Invalid company email format")
    private String companyEmail;

    @Pattern(regexp = "^(https?://)?(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}.*$", message = "Website must be a valid URL")
    private String website;

    @NotNull(message = "Company industry is required")
    private TenantIndustry industry;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    private String password;
}