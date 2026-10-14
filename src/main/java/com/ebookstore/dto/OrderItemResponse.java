package com.ebookstore.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long id,
    Long bookId,
    String bookTitle,
    String author,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice
) {}
