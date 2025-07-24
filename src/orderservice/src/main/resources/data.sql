-- Insert sample data for orders
INSERT INTO orders (user_id, total_amount, status, created_at, updated_at, payment_id) VALUES
(1, 1200.00, 'PENDING_PAYMENT', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), NULL),
(1, 25.50, 'PAID', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'PAY123'),
(2, 15.00, 'PENDING_PAYMENT', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), NULL),
(3, 800.00, 'PAID', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 'PAY456');

-- Insert sample data for order_items
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 1, 1200.00), -- Order 1: Laptop Pro X
(2, 2, 1, 25.50),  -- Order 2: The Great Novel
(3, 3, 1, 15.00),  -- Order 3: Summer T-Shirt
(4, 4, 1, 800.00); -- Order 4: Smartphone Ultra

-- Insert sample data for order_status_history
INSERT INTO order_status_history (order_id, old_status, new_status, changed_at) VALUES
(1, 'DONE', 'PENDING_PAYMENT', CURRENT_TIMESTAMP()),
(2, 'DONE', 'PENDING_PAYMENT', CURRENT_TIMESTAMP()),
(2, 'PENDING_PAYMENT', 'PAID', CURRENT_TIMESTAMP()),
(3, 'DONE', 'PENDING_PAYMENT', CURRENT_TIMESTAMP()),
(4, 'DONE', 'PENDING_PAYMENT', CURRENT_TIMESTAMP()),
(4, 'PENDING_PAYMENT', 'PAID', CURRENT_TIMESTAMP());