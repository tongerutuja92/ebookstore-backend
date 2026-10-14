package com.ebookstore.service;

import com.ebookstore.dto.*;
import com.ebookstore.exception.BadRequestException;
import com.ebookstore.exception.ResourceNotFoundException;
import com.ebookstore.model.*;
import com.ebookstore.repository.OrderRepository;
import com.ebookstore.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public PaymentResponse initiatePayment(Long userId, PaymentInitiateRequest request) {
        Order order = orderRepository.findByIdAndUserId(request.orderId(), userId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.orderId()));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Cannot initiate payment for order in status: " + order.getStatus());
        }

        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        String txnRef = "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        if (payment == null) {
            payment = new Payment(
                order,
                request.paymentMethod(),
                order.getNetPayableAmount(),
                PaymentStatus.PENDING,
                txnRef
            );
        } else {
            payment.setPaymentMethod(request.paymentMethod());
            payment.setAmount(order.getNetPayableAmount());
            payment.setTransactionReference(txnRef);
            payment.setStatus(PaymentStatus.PENDING);
        }

        Payment saved = paymentRepository.save(payment);
        return mapToPaymentResponse(saved);
    }

    public PaymentResponse confirmPayment(Long paymentId, PaymentConfirmRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment record not found with id: " + paymentId));

        payment.setStatus(request.status());
        if (request.transactionReference() != null && !request.transactionReference().isBlank()) {
            payment.setTransactionReference(request.transactionReference());
        }

        Order order = payment.getOrder();
        if (request.status() == PaymentStatus.SUCCESS) {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
        }

        Payment saved = paymentRepository.save(payment);
        return mapToPaymentResponse(saved);
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getOrder().getId(),
            payment.getPaymentMethod(),
            payment.getAmount(),
            payment.getStatus(),
            payment.getTransactionReference(),
            payment.getPaymentDate()
        );
    }
}
