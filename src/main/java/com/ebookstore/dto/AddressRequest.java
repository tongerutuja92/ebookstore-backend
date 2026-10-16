package com.ebookstore.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
    @NotBlank(message = "Recipient name is required")
    String recipientName,

    @NotBlank(message = "Street is required")
    String street,

    @NotBlank(message = "City is required")
    String city,

    @NotBlank(message = "State is required")
    String state,

    @NotBlank(message = "Postal code is required")
    String postalCode,

    String country,
    Boolean isDefault
) {}
