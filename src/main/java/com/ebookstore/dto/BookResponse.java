package com.ebookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookResponse(
    Long id,
    String title,
    String author,
    String isbn,
    String description,
    BigDecimal price,
    Integer stockQuantity,
    Integer deliveryDays,
    LocalDate tentativeDeliveryDate,
    String coverImageUrl,
    Double rating,
    CategoryResponse category,
    BrandResponse brand
) {}
