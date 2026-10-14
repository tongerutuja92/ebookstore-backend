# AI Augmented Test Case Suite & Test Scenario Matrix
**E-Bookstore Platform Backend (Spring Boot 3 / Java 21 / PostgreSQL)**

Generated via **ICA API Test Case Generator & Scenario Matrix Agent** based on OpenAPI Specification and Business Wireframes.

---

## 📊 Test Summary & Coverage Overview

| Module | Total Scenarios | Positive Test Cases | Negative / Edge Cases |
| :--- | :---: | :---: | :---: |
| **1. Authentication & User Profile** | 6 | 3 | 3 |
| **2. Catalogue, Search & Delivery Date** | 8 | 5 | 3 |
| **3. AI Recommendations (Related & Order History)** | 5 | 3 | 2 |
| **4. Shopping Basket (Cart)** | 6 | 4 | 2 |
| **5. Checkout & Gift Points Redemption** | 6 | 3 | 3 |
| **6. 48-Hour Order Cancellation Policy** | 5 | 2 | 3 |
| **7. "Buy It Again" Re-ordering** | 4 | 3 | 1 |
| **8. Multi-Option Payment Processing** | 6 | 3 | 3 |
| **Total** | **46** | **26** | **20** |

---

## 🧪 Detailed Scenario Breakdown

### 1. Authentication & Profile Management

| Test ID | Scenario | Method & Endpoint | Input Payload / Header | Expected Status | Expected Result |
| :--- | :--- | :--- | :--- | :---: | :--- |
| `TC-AUTH-01` | Register new customer successfully | `POST /api/auth/register` | Valid email, password, full name | `201 Created` | Returns auth token, user ID, and 100 bonus gift points |
| `TC-AUTH-02` | Register with duplicate email | `POST /api/auth/register` | Existing email (`john.doe@example.com`) | `400 Bad Request` | Returns error message "User with email already exists" |
| `TC-AUTH-03` | Register with invalid email format | `POST /api/auth/register` | `email: "invalid-email"` | `400 Bad Request` | Validation error on email format |
| `TC-AUTH-04` | User login with valid credentials | `POST /api/auth/login` | Valid email and password | `200 OK` | Returns JWT auth token and user profile |
| `TC-AUTH-05` | User login with wrong password | `POST /api/auth/login` | Valid email, wrong password | `400 Bad Request` | Returns "Invalid email or password" |
| `TC-AUTH-06` | Fetch profile with gift points balance | `GET /api/users/profile` | `X-User-Id: 1` | `200 OK` | Returns user details and gift points balance |

---

### 2. Catalogue, Search & Filtering

| Test ID | Scenario | Method & Endpoint | Query Parameters | Expected Status | Expected Result |
| :--- | :--- | :--- | :--- | :---: | :--- |
| `TC-CAT-01` | List all categories | `GET /api/categories` | None | `200 OK` | Returns 5 seeded categories |
| `TC-CAT-02` | List all publishers/brands | `GET /api/brands` | None | `200 OK` | Returns 5 seeded publishers |
| `TC-CAT-03` | Search books by keyword | `GET /api/books?search=Designing` | `search=Designing` | `200 OK` | Returns matching books (e.g. DDIA, Microservices) |
| `TC-CAT-04` | Filter books by category | `GET /api/books?categoryId=1` | `categoryId=1` | `200 OK` | Returns only Technology books |
| `TC-CAT-05` | Filter books by publisher/brand | `GET /api/books?brandId=1` | `brandId=1` | `200 OK` | Returns only O'Reilly Media books |
| `TC-CAT-06` | Filter books by price range | `GET /api/books?minPrice=15.00&maxPrice=30.00` | Price bounds | `200 OK` | Returns books within $15 to $30 range |
| `TC-CAT-07` | Get book details with tentative delivery date | `GET /api/books/1` | `id=1` | `200 OK` | Returns book details and `tentativeDeliveryDate = today + deliveryDays` |
| `TC-CAT-08` | Get non-existent book | `GET /api/books/999` | `id=999` | `404 Not Found` | Returns "Book not found with id: 999" |

---

### 3. AI / Smart Recommendations

| Test ID | Scenario | Method & Endpoint | Parameters | Expected Status | Expected Result |
| :--- | :--- | :--- | :--- | :---: | :--- |
| `TC-REC-01` | Get related books on product view | `GET /api/books/1/related` | `limit=4` | `200 OK` | Returns top-rated books in same category or by same author |
| `TC-REC-02` | Order-history based recommendations (User with purchases) | `GET /api/recommendations/order-history` | `X-User-Id: 1` | `200 OK` | Suggests books from categories previously bought by user |
| `TC-REC-03` | Order-history recommendations for new user (no orders) | `GET /api/recommendations/order-history` | `X-User-Id: 2` | `200 OK` | Fallbacks to top-rated books across the platform |

---

### 4. Shopping Basket (Cart)

| Test ID | Scenario | Method & Endpoint | Payload / Params | Expected Status | Expected Result |
| :--- | :--- | :--- | :--- | :---: | :--- |
| `TC-CART-01` | Get active user cart | `GET /api/cart` | `X-User-Id: 1` | `200 OK` | Returns active cart items, counts, and subtotal |
| `TC-CART-02` | Add available book to cart | `POST /api/cart/items` | `bookId: 2, quantity: 2` | `200 OK` | Item added, subtotal and quantity updated |
| `TC-CART-03` | Add book exceeding stock quantity | `POST /api/cart/items` | `bookId: 2, quantity: 9999` | `400 Bad Request` | Returns "Requested quantity exceeds available stock" |
| `TC-CART-04` | Update line item quantity | `PUT /api/cart/items/{itemId}` | `quantity: 3` | `200 OK` | Updated quantity reflected in subtotal |
| `TC-CART-05` | Remove single item from cart | `DELETE /api/cart/items/{itemId}` | Line item ID | `200 OK` | Item removed from cart |
| `TC-CART-06` | Clear active cart | `DELETE /api/cart` | `X-User-Id: 1` | `204 No Content`| Cart emptied |

---

### 5. Checkout & Gift Points Redemption

| Test ID | Scenario | Method & Endpoint | Payload | Expected Status | Expected Result |
| :--- | :--- | :--- | :--- | :---: | :--- |
| `TC-ORD-01` | Place order without gift points | `POST /api/orders` | `addressId: 1, pointsToRedeem: 0` | `201 Created` | Order created in `PENDING` status, `canCancel=true` |
| `TC-ORD-02` | Place order with gift points redemption | `POST /api/orders` | `addressId: 1, pointsToRedeem: 200` | `201 Created` | $2.00 discount applied (`200 pts / 100`), points deducted from user |
| `TC-ORD-03` | Place order with points exceeding balance | `POST /api/orders` | `pointsToRedeem: 99999` | `400 Bad Request` | Returns "Requested points to redeem exceeds available balance" |
| `TC-ORD-04` | Place order from empty cart | `POST /api/orders` | Empty cart | `400 Bad Request` | Returns "Cannot create order from an empty cart" |
| `TC-ORD-05` | View order history | `GET /api/orders/history` | `X-User-Id: 1` | `200 OK` | Returns list of past orders with tracking details |

---

### 6. 48-Hour Order Cancellation Policy

| Test ID | Scenario | Method & Endpoint | Conditions | Expected Status | Expected Result |
| :--- | :--- | :--- | :--- | :---: | :--- |
| `TC-CNC-01` | Cancel order within 48 hours | `POST /api/orders/{id}/cancel` | Order created < 48 hours ago | `200 OK` | Status changed to `CANCELLED`, stock restored, gift points refunded |
| `TC-CNC-02` | Cancel order after 48-hour window | `POST /api/orders/1/cancel` | Order created 5 days ago (seed data) | `400 Bad Request` | Returns "Order cannot be cancelled. The 48-hour cancellation window has expired" |
| `TC-CNC-03` | Cancel already cancelled order | `POST /api/orders/{id}/cancel` | Order already CANCELLED | `400 Bad Request` | Returns "Order is already cancelled" |
| `TC-CNC-04` | Cancel shipped/delivered order | `POST /api/orders/1/cancel` | Status = DELIVERED | `400 Bad Request` | Returns "Cannot cancel order in status: DELIVERED" |

---

### 7. "Buy It Again" Re-ordering

| Test ID | Scenario | Method & Endpoint | Conditions | Expected Status | Expected Result |
| :--- | :--- | :--- | :--- | :---: | :--- |
| `TC-BIA-01` | Buy It Again from previous order | `POST /api/orders/1/buy-again` | Valid past order | `200 OK` | Re-populates all items from past order into user cart |
| `TC-BIA-02` | Buy It Again for non-existent order | `POST /api/orders/999/buy-again` | Invalid order ID | `404 Not Found` | Returns "Order not found with id: 999" |

---

### 8. Multi-Option Payment Processing

| Test ID | Scenario | Method & Endpoint | Payload | Expected Status | Expected Result |
| :--- | :--- | :--- | :--- | :---: | :--- |
| `TC-PAY-01` | Initiate Credit Card payment | `POST /api/payments/initiate` | `paymentMethod: "CREDIT_CARD", orderId: {id}` | `200 OK` | Payment initiated with `PENDING` status and `TXN-*` reference |
| `TC-PAY-02` | Initiate UPI payment | `POST /api/payments/initiate` | `paymentMethod: "UPI", orderId: {id}` | `200 OK` | Payment record created |
| `TC-PAY-03` | Confirm successful payment callback | `POST /api/payments/{id}/confirm` | `status: "SUCCESS", transactionReference: "TXN-123"` | `200 OK` | Payment marked `SUCCESS`, order status transitioned to `CONFIRMED` |
| `TC-PAY-04` | Initiate payment for non-pending order | `POST /api/payments/initiate` | Order already CONFIRMED/DELIVERED | `400 Bad Request` | Returns "Cannot initiate payment for order in status: ..." |
