package com.ebookstore.service;

import com.ebookstore.dto.*;
import com.ebookstore.exception.BadRequestException;
import com.ebookstore.exception.ResourceNotFoundException;
import com.ebookstore.model.Book;
import com.ebookstore.model.Brand;
import com.ebookstore.model.Category;
import com.ebookstore.repository.BookRepository;
import com.ebookstore.repository.BrandRepository;
import com.ebookstore.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CatalogService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public CatalogService(BookRepository bookRepository, CategoryRepository categoryRepository, BrandRepository brandRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
    }

    // Categories
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
            .map(this::mapToCategoryResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToCategoryResponse(category);
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new BadRequestException("Category with name '" + request.name() + "' already exists");
        }
        Category category = new Category(request.name(), request.description());
        Category saved = categoryRepository.save(category);
        return mapToCategoryResponse(saved);
    }

    // Brands / Publishers
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll().stream()
            .map(this::mapToBrandResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public BrandResponse getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand/Publisher not found with id: " + id));
        return mapToBrandResponse(brand);
    }

    public BrandResponse createBrand(BrandRequest request) {
        if (brandRepository.existsByNameIgnoreCase(request.name())) {
            throw new BadRequestException("Brand with name '" + request.name() + "' already exists");
        }
        Brand brand = new Brand(request.name(), request.description());
        Brand saved = brandRepository.save(brand);
        return mapToBrandResponse(saved);
    }

    // Books
    @Transactional(readOnly = true)
    public BookPageResponse getBooks(
        String search,
        Long categoryId,
        Long brandId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        int page,
        int size,
        String sortBy,
        String sortOrder
    ) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortProperty = switch (sortBy != null ? sortBy.toLowerCase() : "title") {
            case "price" -> "price";
            case "rating" -> "rating";
            case "createdat" -> "createdAt";
            default -> "title";
        };

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(direction, sortProperty));
        Page<Book> bookPage = bookRepository.searchAndFilter(
            (search != null && !search.isBlank()) ? search.trim() : null,
            categoryId,
            brandId,
            minPrice,
            maxPrice,
            pageable
        );

        List<BookResponse> content = bookPage.getContent().stream()
            .map(this::mapToBookResponse)
            .toList();

        return new BookPageResponse(
            content,
            bookPage.getTotalElements(),
            bookPage.getTotalPages(),
            bookPage.getNumber(),
            bookPage.getSize()
        );
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return mapToBookResponse(book);
    }

    public BookResponse createBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new BadRequestException("Book with ISBN '" + request.isbn() + "' already exists");
        }

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.categoryId()));

        Brand brand = brandRepository.findById(request.brandId())
            .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.brandId()));

        Book book = new Book(
            request.title(),
            request.author(),
            request.isbn(),
            request.description(),
            request.price(),
            request.stockQuantity(),
            request.deliveryDays(),
            request.coverImageUrl(),
            request.rating(),
            category,
            brand
        );

        Book saved = bookRepository.save(book);
        return mapToBookResponse(saved);
    }

    public CategoryResponse mapToCategoryResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }

    public BrandResponse mapToBrandResponse(Brand brand) {
        return new BrandResponse(brand.getId(), brand.getName(), brand.getDescription());
    }

    public BookResponse mapToBookResponse(Book book) {
        int deliveryDays = book.getDeliveryDays() != null ? book.getDeliveryDays() : 3;
        LocalDate tentativeDeliveryDate = LocalDate.now().plusDays(deliveryDays);

        return new BookResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getIsbn(),
            book.getDescription(),
            book.getPrice(),
            book.getStockQuantity(),
            book.getDeliveryDays(),
            tentativeDeliveryDate,
            book.getCoverImageUrl(),
            book.getRating(),
            mapToCategoryResponse(book.getCategory()),
            mapToBrandResponse(book.getBrand())
        );
    }
}
