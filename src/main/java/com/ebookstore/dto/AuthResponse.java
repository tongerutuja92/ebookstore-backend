package com.ebookstore.dto;

public record AuthResponse(
    String token,
    Long userId,
    String email,
    String fullName,
    Integer giftPoints
) {}
