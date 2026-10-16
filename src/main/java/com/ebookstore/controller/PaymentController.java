package com.ebookstore.controller;

import com.ebookstore.dto.PaymentConfirmRequest;
import com.ebookstore.dto.PaymentInitiateRequest;
import com.ebookstore.dto.PaymentResponse;
import com.ebookstore.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Payment initiation and transaction confirmation")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    @Operation(summary = "Initiate payment", description = "Initiate payment for an order with credit card, debit card, UPI, etc.")
    public ResponseEntity<PaymentResponse> initiatePayment(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
        @Valid @RequestBody PaymentInitiateRequest request
    ) {
        PaymentResponse response = paymentService.initiatePayment(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm payment callback", description = "Confirm status of a payment transaction")
    public ResponseEntity<PaymentResponse> confirmPayment(
        @PathVariable Long id,
        @Valid @RequestBody PaymentConfirmRequest request
    ) {
        PaymentResponse response = paymentService.confirmPayment(id, request);
        return ResponseEntity.ok(response);
    }
}
