package com.ebookstore.controller;

import com.ebookstore.dto.BookResponse;
import com.ebookstore.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Recommendations", description = "Related items and personalized recommendations based on order history")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/books/{id}/related")
    @Operation(summary = "Get related books", description = "Returns books in same category or by same author")
    public ResponseEntity<List<BookResponse>> getRelatedBooks(
        @PathVariable Long id,
        @Parameter(description = "Maximum recommendations to return")
        @RequestParam(defaultValue = "4") int limit
    ) {
        List<BookResponse> related = recommendationService.getRelatedBooks(id, limit);
        return ResponseEntity.ok(related);
    }

    @GetMapping("/recommendations/order-history")
    @Operation(summary = "Order-history based recommendations", description = "Suggests books based on user purchase history")
    public ResponseEntity<List<BookResponse>> getOrderHistoryRecommendations(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
        @Parameter(description = "Maximum recommendations to return")
        @RequestParam(defaultValue = "6") int limit
    ) {
        List<BookResponse> recommendations = recommendationService.getRecommendationsBasedOnOrderHistory(userId, limit);
        return ResponseEntity.ok(recommendations);
    }
}
