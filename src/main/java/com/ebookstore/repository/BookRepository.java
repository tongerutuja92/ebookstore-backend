package com.ebookstore.repository;

import com.ebookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE " +
           "(:search IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           " OR LOWER(b.author) LIKE LOWER(CONCAT('%', :search, '%')) " +
           " OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :search, '%')) " +
           " OR LOWER(b.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:categoryId IS NULL OR b.category.id = :categoryId) AND " +
           "(:brandId IS NULL OR b.brand.id = :brandId) AND " +
           "(:minPrice IS NULL OR b.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR b.price <= :maxPrice)")
    Page<Book> searchAndFilter(
        @Param("search") String search,
        @Param("categoryId") Long categoryId,
        @Param("brandId") Long brandId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );

    @Query("SELECT b FROM Book b WHERE b.id <> :bookId AND (b.category.id = :categoryId OR LOWER(b.author) = LOWER(:author)) ORDER BY b.rating DESC")
    List<Book> findRelatedBooks(
        @Param("bookId") Long bookId,
        @Param("categoryId") Long categoryId,
        @Param("author") String author,
        Pageable pageable
    );

    @Query("SELECT b FROM Book b WHERE b.category.id IN :categoryIds OR b.author IN :authors ORDER BY b.rating DESC")
    List<Book> findRecommendations(
        @Param("categoryIds") Collection<Long> categoryIds,
        @Param("authors") Collection<String> authors,
        Pageable pageable
    );

    @Query("SELECT b FROM Book b ORDER BY b.rating DESC")
    List<Book> findTopRated(Pageable pageable);
}
