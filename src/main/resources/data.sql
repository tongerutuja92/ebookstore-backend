-- ==========================================
-- E-Bookstore Initial Seed Data (PostgreSQL)
-- ==========================================

-- 1. Categories
INSERT INTO categories (id, name, description) VALUES
(1, 'Technology & Computer Science', 'Software engineering, cloud architecture, data systems, and AI'),
(2, 'Fiction & Literature', 'Novels, drama, classics, and contemporary storytelling'),
(3, 'Science & Nature', 'Physics, cosmology, biology, and natural sciences'),
(4, 'Business & Leadership', 'Management, startups, finance, and entrepreneurship'),
(5, 'Self-Help & Personal Growth', 'Productivity, mindfulness, psychology, and habits')
ON CONFLICT (id) DO NOTHING;

-- 2. Brands / Publishers
INSERT INTO brands (id, name, description) VALUES
(1, 'O''Reilly Media', 'Leading publisher of expert technology and software engineering books'),
(2, 'Penguin Random House', 'Global publisher of fiction, classics, and non-fiction literature'),
(3, 'Manning Publications', 'Publisher of computer books for developers and architects'),
(4, 'HarperCollins', 'Broad-spectrum publisher of literature, business, and biographies'),
(5, 'Packt Publishing', 'Practical technology guides and hands-on coding books')
ON CONFLICT (id) DO NOTHING;

-- 3. Books
INSERT INTO books (id, title, author, isbn, description, price, stock_quantity, delivery_days, rating, cover_image_url, category_id, brand_id, created_at) VALUES
(1, 'Designing Data-Intensive Applications', 'Martin Kleppmann', '978-1449373320', 'The big ideas behind reliable, scalable, and maintainable data systems.', 44.99, 50, 3, 4.9, 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400', 1, 1, CURRENT_TIMESTAMP),
(2, 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin', '978-0132350884', 'A handbook of agile software craftsmanship with best practices for readable code.', 38.50, 40, 2, 4.7, 'https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=400', 1, 3, CURRENT_TIMESTAMP),
(3, 'Cloud Native Spring in Action', 'Thomas Vitale', '978-1617298639', 'Build resilient, scalable, and observable microservices with Spring Boot and Kubernetes.', 49.99, 35, 4, 4.8, 'https://images.unsplash.com/photo-1512820790803-83ca734da794?w=400', 1, 3, CURRENT_TIMESTAMP),
(4, 'Deep Learning with Python', 'Francois Chollet', '978-1617294433', 'Comprehensive guide to modern AI and deep neural networks with Keras and TensorFlow.', 54.00, 25, 3, 4.8, 'https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?w=400', 1, 3, CURRENT_TIMESTAMP),
(5, 'Building Microservices: Designing Fine-Grained Systems', 'Sam Newman', '978-1492034025', 'Concepts in microservice architectures, modeling, integration, and security.', 42.00, 30, 2, 4.6, 'https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=400', 1, 1, CURRENT_TIMESTAMP),
(6, 'The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 'The story of the fabulously wealthy Jay Gatsby and his love for Daisy Buchanan in the Roaring Twenties.', 15.99, 100, 2, 4.5, 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400', 2, 2, CURRENT_TIMESTAMP),
(7, 'To Kill a Mockingbird', 'Harper Lee', '978-0061120084', 'The unforgettable novel of a childhood in a sleepy Southern town and the crisis of conscience that rocked it.', 14.50, 80, 2, 4.9, 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400', 2, 4, CURRENT_TIMESTAMP),
(8, '1984', 'George Orwell', '978-0451524935', 'A dystopian social science fiction novel and cautionary tale about totalitarianism.', 12.99, 90, 2, 4.8, 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=400', 2, 2, CURRENT_TIMESTAMP),
(9, 'A Brief History of Time', 'Stephen Hawking', '978-0553380163', 'A landmark volume in science writing by one of the greatest minds of our time.', 18.00, 45, 3, 4.7, 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=400', 3, 2, CURRENT_TIMESTAMP),
(10, 'Atomic Habits', 'James Clear', '978-0735211292', 'An easy and proven way to build good habits and break bad ones.', 22.00, 120, 1, 4.9, 'https://images.unsplash.com/photo-1544717305-2782549b5136?w=400', 5, 2, CURRENT_TIMESTAMP),
(11, 'The Lean Startup', 'Eric Ries', '978-0307887894', 'How constant innovation creates radically successful businesses.', 26.50, 60, 3, 4.6, 'https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=400', 4, 4, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 4. Demo Users
INSERT INTO users (id, email, password, full_name, phone_number, gift_points, created_at) VALUES
(1, 'john.doe@example.com', 'demo_user_auth_token_1', 'John Doe', '+1-555-0199', 500, CURRENT_TIMESTAMP),
(2, 'jane.smith@example.com', 'demo_user_auth_token_2', 'Jane Smith', '+1-555-0288', 200, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 5. User Addresses
INSERT INTO addresses (id, user_id, recipient_name, street, city, state, postal_code, country, is_default) VALUES
(1, 1, 'John Doe', '100 Silicon Valley Way, Suite 400', 'San Jose', 'CA', '95134', 'USA', true),
(2, 1, 'John Doe (Home)', '250 Maple Street', 'Palo Alto', 'CA', '94301', 'USA', false),
(3, 2, 'Jane Smith', '45 Broadway Avenue', 'New York', 'NY', '10006', 'USA', true)
ON CONFLICT (id) DO NOTHING;

-- 6. User Active Carts
INSERT INTO carts (id, user_id, updated_at) VALUES
(1, 1, CURRENT_TIMESTAMP),
(2, 2, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Sample cart item for John Doe
INSERT INTO cart_items (id, cart_id, book_id, quantity, unit_price) VALUES
(1, 1, 1, 1, 44.99)
ON CONFLICT (id) DO NOTHING;

-- 7. Sample Previous Orders for John Doe
INSERT INTO orders (id, order_number, user_id, address_id, gross_amount, discount_amount, points_redeemed, net_payable_amount, status, created_at, tentative_delivery_date) VALUES
(1, 'ORD-20250501-1001', 1, 1, 49.99, 5.00, 500, 44.99, 'DELIVERED', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_DATE - 2),
(2, 'ORD-20250510-1002', 1, 1, 38.50, 0.00, 0, 38.50, 'CONFIRMED', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_DATE + 3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO order_items (id, order_id, book_id, quantity, unit_price, total_price) VALUES
(1, 1, 3, 1, 49.99, 49.99),
(2, 2, 2, 1, 38.50, 38.50)
ON CONFLICT (id) DO NOTHING;

-- Payments
INSERT INTO payments (id, order_id, payment_method, amount, status, transaction_reference, payment_date) VALUES
(1, 1, 'CREDIT_CARD', 44.99, 'SUCCESS', 'TXN-9876543210', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(2, 2, 'UPI', 38.50, 'SUCCESS', 'TXN-9876543211', CURRENT_TIMESTAMP - INTERVAL '2 hours')
ON CONFLICT (id) DO NOTHING;
