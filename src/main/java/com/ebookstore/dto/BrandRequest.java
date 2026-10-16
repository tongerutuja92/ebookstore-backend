package com.ebookstore.dto;

import jakarta.validation.constraints.NotBlank;

public record BrandRequest(
    @NotBlank(message = "Brand name is required")
    String name,
    String description
) {}
