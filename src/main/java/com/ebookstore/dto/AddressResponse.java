package com.ebookstore.dto;

public record AddressResponse(
    Long id,
    Long userId,
    String recipientName,
    String street,
    String city,
    String state,
    String postalCode,
    String country,
    Boolean isDefault
) {}
