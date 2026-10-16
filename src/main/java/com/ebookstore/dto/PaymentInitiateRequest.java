package com.ebookstore.dto;

import com.ebookstore.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PaymentInitiateRequest(
    @NotNull(message = "Order ID is required")
    Long orderId,

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod,

    String cardHolderName,
    String cardNumber
) {}
