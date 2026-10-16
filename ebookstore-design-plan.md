# E-Bookstore Platform Backend (Spring Boot 3 & Java 21) - Architecture & Implementation Summary

## Overview
The backend application has been generated for **Spring Boot 3.3.4** and **Java 21** with **Spring Data JPA**, **PostgreSQL / H2**, **SpringDoc OpenAPI 3.0 (Swagger UI)**, and validation according to the Capstone project wireframes and user journeys.

---

## Architecture & Components

```
+-----------------------------------------------------------------------------------------------+
|                                      REST Controller Layer                                    |
| [AuthController] [UserController] [CatalogController] [CartController] [OrderController]    |
| [PaymentController] [RecommendationController] [GlobalExceptionHandler]                       |
+-----------------------------------------------------------------------------------------------+
                                                |
+-----------------------------------------------------------------------------------------------+
|                                       Service Business Logic                                  |
| - UserService (auth, profiles, multi-address)                                                 |
| - CatalogService (faceted search, categories, brands, delivery date estimation)               |
| - RecommendationService (related books, order-history based AI recommendations)               |
| - CartService (basket management, stock check)                                                |
| - OrderService (placement, loyalty points redemption, 48h cancellation rule, Buy It Again)    |
| - PaymentService (multi-method initiation, payment confirmation)                              |
+-----------------------------------------------------------------------------------------------+
                                                |
+-----------------------------------------------------------------------------------------------+
|                                  Spring Data JPA Repositories                                 |
| UserRepository, AddressRepository, CategoryRepository, BrandRepository, BookRepository,      |
| CartRepository, CartItemRepository, OrderRepository, OrderItemRepository, PaymentRepository  |
+-----------------------------------------------------------------------------------------------+
                                                |
+-----------------------------------------------------------------------------------------------+
|                                         Data Entities                                         |
| User, Address, Category, Brand, Book, Cart, CartItem, Order, OrderItem, Payment             |
+-----------------------------------------------------------------------------------------------+
                                                |
+-----------------------------------------------------------------------------------------------+
|                                  Persistence (PostgreSQL / H2)                                |
| Initial Seed Data: 5 Categories, 5 Publishers, 11 Books, Demo Users, Sample Orders & Payments |
+-----------------------------------------------------------------------------------------------+
```

---

## File Structure

- [`pom.xml`](pom.xml) — Maven project configuration with Java 21 and Spring Boot 3.3.4
- [`openapi.yaml`](openapi.yaml) — OpenAPI 3.0 specification covering all endpoints
- [`README.md`](README.md) — Comprehensive guide, API reference, running instructions
- `src/main/resources/application.properties` — Datasource & Swagger configuration
- `src/main/resources/data.sql` — Seed data for categories, publishers, books, users, and orders
- `src/main/java/com/ebookstore/`
  - `EBookstoreApplication.java` — Main entry point
  - `model/` — JPA entities (`User`, `Address`, `Category`, `Brand`, `Book`, `Cart`, `CartItem`, `Order`, `OrderItem`, `Payment`) and enums (`OrderStatus`, `PaymentMethod`, `PaymentStatus`)
  - `dto/` — Request / Response DTO records for all APIs
  - `repository/` — Spring Data JPA repositories with custom queries
  - `service/` — Business logic implementations
  - `controller/` — REST controllers with Swagger annotations
  - `exception/` — Unified error handling & `GlobalExceptionHandler`
  - `config/` — `OpenApiConfig` and `WebConfig` (CORS)
- `src/test/java/com/ebookstore/` — Integration & unit tests
