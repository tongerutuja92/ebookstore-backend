# E-Bookstore Platform Backend

RESTful backend service for the E-Commerce Book Store platform built with **Spring Boot 3.3.4** and **Java 21**, implementing all user journeys, business rules, and OpenAPI 3.0 endpoints.

---

## 🚀 Key Features & Implemented Customer Journeys

1. **User Authentication & Profiles**:
   - Customer registration with default bonus gift points (`POST /api/auth/register`)
   - Customer login and credential verification (`POST /api/auth/login`)
   - Profile management with gift points balance tracking (`GET /api/users/profile`)
   - Multi-address management (`GET`, `POST`, `PUT`, `DELETE /api/users/addresses`)

2. **Product Catalogue, Search & Filtering**:
   - Filter and browse by Category (Fiction, Technology, Science, Business, Self-Help)
   - Filter and browse by Publisher / Brand (O'Reilly, Penguin, Manning, HarperCollins, Packt)
   - Multi-facet search (keyword in title/author/description/ISBN, price range, rating, pagination, sorting)
   - Book details with tentative delivery date calculation

3. **Smart Recommendations**:
   - Related book suggestions on product detail view (`GET /api/books/{id}/related`)
   - Personalized recommendations based on user order history (`GET /api/recommendations/order-history`)

4. **Shopping Cart**:
   - View active basket, item counts, unit prices, and subtotal (`GET /api/cart`)
   - Add books with real-time stock validation (`POST /api/cart/items`)
   - Update line-item quantity (`PUT /api/cart/items/{itemId}`)
   - Line-item removal and basket clearance (`DELETE /api/cart`)

5. **Orders, 48-Hour Cancellation & "Buy It Again"**:
   - Place order with delivery address selection and **gift points redemption** (100 points = $1.00) (`POST /api/orders`)
   - View order history and order tracking (`GET /api/orders/history`, `GET /api/orders/{id}`)
   - **48-Hour Order Cancellation Policy**: Orders can be cancelled strictly within 48 hours of creation, automatically restocking inventory and refunding redeemed gift points (`POST /api/orders/{id}/cancel`)
   - **"Buy It Again"**: One-click re-population of previous order items into active shopping basket (`POST /api/orders/{id}/buy-again`)

6. **Payment Processing**:
   - Multi-option payment initiation (Credit Card, Debit Card, UPI, Net Banking, Gift Points) (`POST /api/payments/initiate`)
   - Payment confirmation and automated order status progression (`POST /api/payments/{id}/confirm`)

---

## 🛠 Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.3.4 (Spring Web, Spring Data JPA, Spring Validation)
- **Database**: PostgreSQL (Production) / H2 in-memory mode (Local zero-config dev & testing)
- **API Documentation**: SpringDoc OpenAPI 3.0 / Swagger UI
- **Build Tool**: Maven

---

## 📋 Prerequisites

- **Java 21** or later (`java -version`)
- **Maven 3.8+** (`mvn -v`)

---

## 🏃 Running the Application

### 1. Build & Test
```bash
mvn clean install
```

### 2. Run Locally (using in-memory H2 with seed data)
```bash
mvn spring-boot:run
```
The server will start on `http://localhost:8080`.

### 3. Connect to PostgreSQL (Optional)
In `src/main/resources/application.properties`, uncomment the PostgreSQL configuration:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ebookstoredb
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

---

## 📖 Interactive API Documentation (Swagger UI)

Once the application is running, open your browser to access the interactive Swagger UI and OpenAPI documentation:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **Root OpenAPI YAML**: [`openapi.yaml`](openapi.yaml)

---

## 🧪 Preloaded Seed Data & Demo Accounts

The database comes pre-seeded with 11 books across 5 categories and 5 publishers, plus demo user accounts:

| User Email | Gift Points | Preloaded Orders |
| :--- | :--- | :--- |
| `john.doe@example.com` | 500 | 2 past orders |
| `jane.smith@example.com` | 200 | 0 orders |
