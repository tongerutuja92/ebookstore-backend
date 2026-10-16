package com.ebookstore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BookRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Author is required")
    String author,

    @NotBlank(message = "ISBN is required")
    String isbn,

    String description,

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    BigDecimal price,

    Integer stockQuantity,
    Integer deliveryDays,
    String coverImageUrl,
    Double rating,

    @NotNull(message = "Category ID is required")
    Long categoryId,

    @NotNull(message = "Brand ID is required")
    Long brandId
) {}
