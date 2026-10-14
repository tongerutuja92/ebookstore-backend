package com.ebookstore.dto;

import java.util.List;

public record BookPageResponse(
    List<BookResponse> content,
    long totalElements,
    int totalPages,
    int currentPage,
    int pageSize
) {}
