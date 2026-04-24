package com.dxc.tenantservice.application.dto.tenant.req;

import com.dxc.tenantservice.domain.model.valueobjects.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTenantReqDTO {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Pattern(
            regexp = "^(https?://)?(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}.*$",
            message = "Website must be a valid URL"
    )
    private String website;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Pattern(
            regexp = "^(https?://).+",
            message = "Logo must be a valid URL"
    )
    private String logo;

    @Valid
    private Address address;

    @Min(value = 1, message = "Company size must be at least 1")
    @Max(value = 1_000_000, message = "Company size is unrealistically large")
    private int size;

    @PastOrPresent(message = "Founded date cannot be in the future")
    private LocalDate foundedDate;
}