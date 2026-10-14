package com.ebookstore.controller;

import com.ebookstore.dto.CartResponse;
import com.ebookstore.dto.CreateOrderRequest;
import com.ebookstore.dto.OrderResponse;
import com.ebookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders & Checkout", description = "Order placement, gift points redemption, 48h cancellation, and Buy It Again")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Place order", description = "Converts current shopping cart into an order, applies gift points discount, checks stock")
    public ResponseEntity<OrderResponse> createOrder(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
        @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse order = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/history")
    @Operation(summary = "Order history", description = "Retrieves all past orders for the user")
    public ResponseEntity<List<OrderResponse>> getOrderHistory(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId
    ) {
        List<OrderResponse> history = orderService.getOrderHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieves specific order details")
    public ResponseEntity<OrderResponse> getOrderById(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
        @PathVariable Long id
    ) {
        OrderResponse order = orderService.getOrderById(userId, id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel order (within 48 hours)", description = "Cancels an order if within 48h window, refunds points, and restores stock")
    public ResponseEntity<OrderResponse> cancelOrder(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
        @PathVariable Long id
    ) {
        OrderResponse order = orderService.cancelOrder(userId, id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/buy-again")
    @Operation(summary = "Buy It Again", description = "Re-adds all items from a past order into active cart")
    public ResponseEntity<CartResponse> buyItAgain(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
        @PathVariable Long id
    ) {
        CartResponse cart = orderService.buyItAgain(userId, id);
        return ResponseEntity.ok(cart);
    }
}
