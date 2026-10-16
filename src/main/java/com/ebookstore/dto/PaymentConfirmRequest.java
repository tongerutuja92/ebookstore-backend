package com.ebookstore.dto;

import com.ebookstore.model.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentConfirmRequest(
    @NotBlank(message = "Transaction reference is required")
    String transactionReference,

    @NotNull(message = "Payment status is required")
    PaymentStatus status
) {}
