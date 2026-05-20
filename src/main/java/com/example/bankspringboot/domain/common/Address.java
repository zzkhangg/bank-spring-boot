package com.example.bankspringboot.domain.common;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Address {

  @NotBlank(message = "Street is required.")
  private String street;

  @NotBlank(message = "City is required.")
  private String city;

  @NotBlank(message = "Postal code is required.")
  @Size(min = 2, max = 12, message = "Postal code must be between 2 and 12 characters.")
  private String postalCode;

  @NotBlank(message = "Country is required.")
  @Size(max = 100, message = "Country cannot exceed 100 characters.")
  private String country;
}
