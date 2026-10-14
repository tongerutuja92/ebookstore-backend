package com.ebookstore.service;

import com.ebookstore.dto.BookResponse;
import com.ebookstore.exception.ResourceNotFoundException;
import com.ebookstore.model.Book;
import com.ebookstore.model.Order;
import com.ebookstore.model.OrderItem;
import com.ebookstore.repository.BookRepository;
import com.ebookstore.repository.OrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class RecommendationService {

    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final CatalogService catalogService;

    public RecommendationService(BookRepository bookRepository, OrderRepository orderRepository, CatalogService catalogService) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.catalogService = catalogService;
    }

    public List<BookResponse> getRelatedBooks(Long bookId, int limit) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        int max = Math.max(limit, 1);
        List<Book> related = bookRepository.findRelatedBooks(
            book.getId(),
            book.getCategory().getId(),
            book.getAuthor(),
            PageRequest.of(0, max)
        );

        if (related.isEmpty()) {
            related = bookRepository.findTopRated(PageRequest.of(0, max));
        }

        return related.stream()
            .map(catalogService::mapToBookResponse)
            .toList();
    }

    public List<BookResponse> getRecommendationsBasedOnOrderHistory(Long userId, int limit) {
        int max = Math.max(limit, 1);
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

        Set<Long> categoryIds = new HashSet<>();
        Set<String> authors = new HashSet<>();
        Set<Long> purchasedBookIds = new HashSet<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                if (item.getBook() != null) {
                    purchasedBookIds.add(item.getBook().getId());
                    if (item.getBook().getCategory() != null) {
                        categoryIds.add(item.getBook().getCategory().getId());
                    }
                    if (item.getBook().getAuthor() != null) {
                        authors.add(item.getBook().getAuthor());
                    }
                }
            }
        }

        List<Book> recommendations;
        if (!categoryIds.isEmpty() || !authors.isEmpty()) {
            recommendations = bookRepository.findRecommendations(
                categoryIds.isEmpty() ? List.of(-1L) : categoryIds,
                authors.isEmpty() ? List.of("__NONE__") : authors,
                PageRequest.of(0, max + purchasedBookIds.size())
            ).stream()
             .filter(b -> !purchasedBookIds.contains(b.getId()))
             .limit(max)
             .toList();
        } else {
            recommendations = bookRepository.findTopRated(PageRequest.of(0, max));
        }

        if (recommendations.isEmpty()) {
            recommendations = bookRepository.findTopRated(PageRequest.of(0, max));
        }

        return recommendations.stream()
            .map(catalogService::mapToBookResponse)
            .toList();
    }
}
