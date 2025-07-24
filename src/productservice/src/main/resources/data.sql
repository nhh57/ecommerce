-- Insert sample data for product_categories
INSERT INTO product_categories (name, created_at, updated_at) VALUES
('Electronics', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('Books', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('Clothing', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Insert sample data for products
INSERT INTO products (name, description, price, display_status, category_id, created_at, updated_at) VALUES
('Laptop Pro X', 'Powerful laptop for professionals', 1200.00, 'Còn hàng', 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('The Great Novel', 'A captivating story', 25.50, 'Còn hàng', 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('Summer T-Shirt', 'Lightweight and comfortable t-shirt', 15.00, 'Còn hàng', 3, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('Smartphone Ultra', 'Latest generation smartphone with advanced features', 800.00, 'Còn hàng', 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('Cookbook Basics', 'Essential recipes for beginners', 30.00, 'Còn hàng', 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Insert sample data for product_image_urls (assuming product_id 1 is Laptop Pro X)
INSERT INTO product_image_urls (product_id, image_url) VALUES
(1, 'http://example.com/images/laptop_pro_x_1.jpg'),
(1, 'http://example.com/images/laptop_pro_x_2.jpg');

-- Insert sample data for product_attributes
INSERT INTO product_attributes (product_id, attribute_name, attribute_value, created_at, updated_at) VALUES
(1, 'Processor', 'Intel i7', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(1, 'RAM', '16GB', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(1, 'Storage', '512GB SSD', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(2, 'Author', 'Jane Doe', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(2, 'Genre', 'Fiction', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(3, 'Size', 'M', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(3, 'Color', 'Blue', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(4, 'Screen Size', '6.7 inch', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(4, 'Camera', '48MP', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());