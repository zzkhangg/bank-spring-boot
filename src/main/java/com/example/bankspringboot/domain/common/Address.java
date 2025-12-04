package com.example.bankspringboot.domain.common;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
public class Address {
    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "\\d{5}(-\\d{4})?", message = "Postal code must be 5 digits or 5+4 format (12345 or 12345-6789)")
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country name must be at most 100 characters")
    private String country;
}
