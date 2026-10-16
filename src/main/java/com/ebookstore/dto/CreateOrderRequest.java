package com.ebookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull(message = "Address ID is required")
    Long addressId,

    @Min(value = 0, message = "Points to redeem cannot be negative")
    Integer pointsToRedeem
) {}
