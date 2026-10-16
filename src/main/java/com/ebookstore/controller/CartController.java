package com.ebookstore.controller;

import com.ebookstore.dto.AddCartItemRequest;
import com.ebookstore.dto.CartResponse;
import com.ebookstore.dto.UpdateCartItemRequest;
import com.ebookstore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Shopping Cart", description = "Shopping basket management, add/update/remove items")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Get current cart", description = "Retrieves active shopping cart for user")
    public ResponseEntity<CartResponse> getCart(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId
    ) {
        CartResponse cart = cartService.getCartResponse(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Adds a book to the shopping cart with quantity validation")
    public ResponseEntity<CartResponse> addItem(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
        @Valid @RequestBody AddCartItemRequest request
    ) {
        CartResponse cart = cartService.addItemToCart(userId, request);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity", description = "Updates quantity for a specific line item")
    public ResponseEntity<CartResponse> updateItem(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
        @PathVariable Long itemId,
        @Valid @RequestBody UpdateCartItemRequest request
    ) {
        CartResponse cart = cartService.updateItemQuantity(userId, itemId, request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart", description = "Removes a specific line item from the cart")
    public ResponseEntity<CartResponse> removeItem(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
        @PathVariable Long itemId
    ) {
        CartResponse cart = cartService.removeItem(userId, itemId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Removes all items from the shopping cart")
    public ResponseEntity<Void> clearCart(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId
    ) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
