package com.dxc.tenantservice.application.dto.tenant.req;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressReqDTO {

    @Size(min = 3, max = 150, message = "Street must be between 3 and 150 characters")
    private String street;

    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;

    @Size(min = 2, max = 100, message = "State must be between 2 and 100 characters")
    private String state;

    @Pattern(regexp = "^[A-Za-z0-9\\-\\s]{3,12}$", message = "Zip code must be 3–12 characters and alphanumeric")
    private String zipCode;

    @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
    private String country;
}