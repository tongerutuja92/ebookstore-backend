package com.ebookstore.dto;

import java.time.Instant;

public record UserProfileResponse(
    Long id,
    String email,
    String fullName,
    String phoneNumber,
    Integer giftPoints,
    Instant createdAt
) {}
