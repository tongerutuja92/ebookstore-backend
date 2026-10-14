package com.ebookstore.service;

import com.ebookstore.dto.*;
import com.ebookstore.exception.BadRequestException;
import com.ebookstore.exception.ResourceNotFoundException;
import com.ebookstore.model.*;
import com.ebookstore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            Cart newCart = new Cart(user);
            return cartRepository.save(newCart);
        });
    }

    @Transactional(readOnly = true)
    public CartResponse getCartResponse(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToCartResponse(cart);
    }

    public CartResponse addItemToCart(Long userId, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        Book book = bookRepository.findById(request.bookId())
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.bookId()));

        if (book.getStockQuantity() < request.quantity()) {
            throw new BadRequestException("Requested quantity (" + request.quantity() + ") exceeds available stock (" + book.getStockQuantity() + ")");
        }

        CartItem existingItem = cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId()).orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.quantity();
            if (book.getStockQuantity() < newQuantity) {
                throw new BadRequestException("Total requested quantity (" + newQuantity + ") exceeds available stock (" + book.getStockQuantity() + ")");
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setUnitPrice(book.getPrice());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem(cart, book, request.quantity(), book.getPrice());
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        return mapToCartResponse(cart);
    }

    public CartResponse updateItemQuantity(Long userId, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        if (item.getBook().getStockQuantity() < request.quantity()) {
            throw new BadRequestException("Requested quantity (" + request.quantity() + ") exceeds available stock (" + item.getBook().getStockQuantity() + ")");
        }

        item.setQuantity(request.quantity());
        cartItemRepository.save(item);

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        return mapToCartResponse(cart);
    }

    public CartResponse removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        return mapToCartResponse(cart);
    }

    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
    }

    public CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalItemCount = 0;

        for (CartItem item : cart.getItems()) {
            BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);
            totalItemCount += item.getQuantity();

            itemResponses.add(new CartItemResponse(
                item.getId(),
                item.getBook().getId(),
                item.getBook().getTitle(),
                item.getBook().getAuthor(),
                item.getBook().getCoverImageUrl(),
                item.getUnitPrice(),
                item.getQuantity(),
                itemTotal
            ));
        }

        return new CartResponse(
            cart.getId(),
            cart.getUser().getId(),
            itemResponses,
            totalItemCount,
            subtotal
        );
    }
}
