package com.ebookstore.service;

import com.ebookstore.dto.*;
import com.ebookstore.exception.BadRequestException;
import com.ebookstore.exception.ResourceNotFoundException;
import com.ebookstore.model.*;
import com.ebookstore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final BookRepository bookRepository;
    private final CartService cartService;
    private final UserService userService;

    public OrderService(
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository,
        UserRepository userRepository,
        AddressRepository addressRepository,
        BookRepository bookRepository,
        CartService cartService,
        UserService userService
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.bookRepository = bookRepository;
        this.cartService = cartService;
        this.userService = userService;
    }

    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        User user = userService.getUserOrThrow(userId);
        Cart cart = cartService.getOrCreateCart(userId);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot create order from an empty cart");
        }

        Address address = addressRepository.findByIdAndUserId(request.addressId(), userId)
            .orElseThrow(() -> new BadRequestException("Invalid delivery address ID: " + request.addressId()));

        // Validate stock & calculate totals
        BigDecimal grossAmount = BigDecimal.ZERO;
        int maxDeliveryDays = 3;

        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            if (book.getStockQuantity() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for book: " + book.getTitle() + " (Available: " + book.getStockQuantity() + ")");
            }
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            grossAmount = grossAmount.add(itemTotal);
            if (book.getDeliveryDays() != null && book.getDeliveryDays() > maxDeliveryDays) {
                maxDeliveryDays = book.getDeliveryDays();
            }
        }

        // Handle gift points redemption (100 points = $1.00)
        int requestedPoints = request.pointsToRedeem() != null ? request.pointsToRedeem() : 0;
        if (requestedPoints > user.getGiftPoints()) {
            throw new BadRequestException("Requested points to redeem (" + requestedPoints + ") exceeds available balance (" + user.getGiftPoints() + ")");
        }

        BigDecimal discountAmount = BigDecimal.valueOf(requestedPoints)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        if (discountAmount.compareTo(grossAmount) > 0) {
            discountAmount = grossAmount;
            requestedPoints = grossAmount.multiply(BigDecimal.valueOf(100)).intValue();
        }

        BigDecimal netPayableAmount = grossAmount.subtract(discountAmount);

        // Deduct points from user
        user.setGiftPoints(user.getGiftPoints() - requestedPoints);
        userRepository.save(user);

        // Create Order
        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + (100 + (int)(Math.random() * 900));
        LocalDate tentativeDeliveryDate = LocalDate.now().plusDays(maxDeliveryDays);

        Order order = new Order(
            orderNumber,
            user,
            address,
            grossAmount,
            discountAmount,
            requestedPoints,
            netPayableAmount,
            tentativeDeliveryDate
        );
        Order savedOrder = orderRepository.save(order);

        // Create Order Items & deduct inventory
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            book.setStockQuantity(book.getStockQuantity() - item.getQuantity());
            bookRepository.save(book);

            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            OrderItem orderItem = new OrderItem(savedOrder, book, item.getQuantity(), item.getUnitPrice(), lineTotal);
            orderItems.add(orderItemRepository.save(orderItem));
        }

        savedOrder.setItems(orderItems);

        // Empty cart
        cartService.clearCart(userId);

        return mapToOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderHistory(Long userId) {
        userService.getUserOrThrow(userId);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(this::mapToOrderResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return mapToOrderResponse(order);
    }

    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.isCancellable()) {
            if (order.getStatus() == OrderStatus.CANCELLED) {
                throw new BadRequestException("Order is already cancelled");
            } else if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
                throw new BadRequestException("Cannot cancel order in status: " + order.getStatus());
            } else {
                throw new BadRequestException("Order cannot be cancelled. The 48-hour cancellation window has expired.");
            }
        }

        order.setStatus(OrderStatus.CANCELLED);

        // Restore book stock
        for (OrderItem item : order.getItems()) {
            Book book = item.getBook();
            book.setStockQuantity(book.getStockQuantity() + item.getQuantity());
            bookRepository.save(book);
        }

        // Refund redeemed points
        if (order.getPointsRedeemed() != null && order.getPointsRedeemed() > 0) {
            User user = order.getUser();
            user.setGiftPoints(user.getGiftPoints() + order.getPointsRedeemed());
            userRepository.save(user);
        }

        Order saved = orderRepository.save(order);
        return mapToOrderResponse(saved);
    }

    public CartResponse buyItAgain(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        for (OrderItem item : order.getItems()) {
            cartService.addItemToCart(userId, new AddCartItemRequest(item.getBook().getId(), item.getQuantity()));
        }

        return cartService.getCartResponse(userId);
    }

    public OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
            .map(item -> new OrderItemResponse(
                item.getId(),
                item.getBook().getId(),
                item.getBook().getTitle(),
                item.getBook().getAuthor(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
            ))
            .toList();

        AddressResponse addressResponse = userService.mapToAddressResponse(order.getAddress());

        return new OrderResponse(
            order.getId(),
            order.getOrderNumber(),
            order.getUser().getId(),
            addressResponse,
            itemResponses,
            order.getGrossAmount(),
            order.getPointsRedeemed(),
            order.getDiscountAmount(),
            order.getNetPayableAmount(),
            order.getStatus(),
            order.getCreatedAt(),
            order.getTentativeDeliveryDate(),
            order.isCancellable()
        );
    }
}
