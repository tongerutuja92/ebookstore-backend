package com.ebookstore.dto;

import com.ebookstore.model.PaymentMethod;
import com.ebookstore.model.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
    Long id,
    Long orderId,
    PaymentMethod paymentMethod,
    BigDecimal amount,
    PaymentStatus status,
    String transactionReference,
    Instant paymentDate
) {}
