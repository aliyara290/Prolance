package com.dxc.tenantservice.application.dto.user.req;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;
import com.dxc.tenantservice.domain.model.valueobject.UserRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserReqDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must contain only letters, numbers, and underscores")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    private String password;

    @NotEmpty(message = "At least one role is required")
    private Set<UserRole> roles;

    @Size(max = 100, message = "Job title must not exceed 100 characters")
    private String jobTitle;

    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;
}
