package com.ebookstore.dto;

import java.math.BigDecimal;

public record CartItemResponse(
    Long id,
    Long bookId,
    String bookTitle,
    String bookAuthor,
    String coverImageUrl,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal itemTotal
) {}
