package com.dxc.tenantservice.domain.model.valueobjects;

import lombok.Getter;

import static java.util.Objects.hash;

@Getter
public class Address {

    private final String street;
    private final String city;
    private final String state;
    private final String zipCode;
    private final String country;

    public Address(
            String street,
            String city,
            String state,
            String zipCode,
            String country
    ) {
        validate(city, country);

        this.street = normalize(street);
        this.city = normalize(city);
        this.state = normalize(state);
        this.zipCode = normalize(zipCode);
        this.country = normalize(country);
    }

    private void validate(String city, String country) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City is required");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country is required");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address that)) return false;

        return street.equals(that.street) &&
                city.equals(that.city) &&
                state.equals(that.state) &&
                zipCode.equals(that.zipCode) &&
                country.equals(that.country);
    }

    @Override
    public int hashCode() {
        return hash(street, city, state, zipCode, country);
    }
}