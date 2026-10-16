package com.ebookstore.repository;

import com.ebookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndBookId(Long cartId, Long bookId);
    Optional<CartItem> findByIdAndCartId(Long id, Long cartId);
    void deleteByCartId(Long cartId);
}
