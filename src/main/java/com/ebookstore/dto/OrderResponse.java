package com.ebookstore.dto;

import com.ebookstore.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record OrderResponse(
    Long id,
    String orderNumber,
    Long userId,
    AddressResponse deliveryAddress,
    List<OrderItemResponse> items,
    BigDecimal grossAmount,
    Integer pointsRedeemed,
    BigDecimal discountAmount,
    BigDecimal netPayableAmount,
    OrderStatus status,
    Instant createdAt,
    LocalDate tentativeDeliveryDate,
    boolean canCancel
) {}
