package com.ebookstore.controller;

import com.ebookstore.dto.*;
import com.ebookstore.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Catalog & Books", description = "Browse categories, publishers/brands, search and filter books")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // Categories
    @GetMapping("/categories")
    @Operation(summary = "List all categories", description = "Returns all available book categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(catalogService.getAllCategories());
    }

    @GetMapping("/categories/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getCategoryById(id));
    }

    @PostMapping("/categories")
    @Operation(summary = "Create a category")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogService.createCategory(request));
    }

    // Brands / Publishers
    @GetMapping("/brands")
    @Operation(summary = "List all publishers/brands")
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        return ResponseEntity.ok(catalogService.getAllBrands());
    }

    @GetMapping("/brands/{id}")
    @Operation(summary = "Get brand by ID")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getBrandById(id));
    }

    @PostMapping("/brands")
    @Operation(summary = "Create a brand/publisher")
    public ResponseEntity<BrandResponse> createBrand(@Valid @RequestBody BrandRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogService.createBrand(request));
    }

    // Books
    @GetMapping("/books")
    @Operation(summary = "Search and filter books", description = "Search books by keyword, category, publisher, price range, with pagination and sorting")
    public ResponseEntity<BookPageResponse> getBooks(
        @Parameter(description = "Keyword search in title, author, description, or ISBN")
        @RequestParam(required = false) String search,
        @Parameter(description = "Filter by category ID")
        @RequestParam(required = false) Long categoryId,
        @Parameter(description = "Filter by brand/publisher ID")
        @RequestParam(required = false) Long brandId,
        @Parameter(description = "Minimum price")
        @RequestParam(required = false) BigDecimal minPrice,
        @Parameter(description = "Maximum price")
        @RequestParam(required = false) BigDecimal maxPrice,
        @Parameter(description = "Page number (0-based)")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size")
        @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Sort field (title, price, rating, createdAt)")
        @RequestParam(defaultValue = "title") String sortBy,
        @Parameter(description = "Sort direction (asc, desc)")
        @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        BookPageResponse response = catalogService.getBooks(
            search, categoryId, brandId, minPrice, maxPrice, page, size, sortBy, sortOrder
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/books/{id}")
    @Operation(summary = "Get book details by ID", description = "Returns full details of a book including calculated tentative delivery date")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getBookById(id));
    }

    @PostMapping("/books")
    @Operation(summary = "Add a new book to catalog")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogService.createBook(request));
    }
}
