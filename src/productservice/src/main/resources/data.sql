-- Insert sample data for product_categories
INSERT INTO product_categories (name) VALUES
('Electronics'),
('Books'),
('Clothing');

-- Insert sample data for products
INSERT INTO products (name, description, price, display_status, category_id, image_urls, created_at, updated_at) VALUES
('Laptop Pro X', 'Powerful laptop for professionals', 1200.00, 'Còn hàng', 1, 'http://example.com/images/laptop_pro_x_1.jpg,http://example.com/images/laptop_pro_x_2.jpg', NOW(), NOW()),
('The Great Novel', 'A captivating story', 25.50, 'Còn hàng', 2, 'http://example.com/images/the_great_novel.jpg', NOW(), NOW()),
('Summer T-Shirt', 'Lightweight and comfortable t-shirt', 15.00, 'Còn hàng', 3, 'http://example.com/images/summer_tshirt.jpg', NOW(), NOW()),
('Smartphone Ultra', 'Latest generation smartphone with advanced features', 800.00, 'Còn hàng', 1, 'http://example.com/images/smartphone_ultra.jpg', NOW(), NOW()),
('Cookbook Basics', 'Essential recipes for beginners', 30.00, 'Còn hàng', 2, 'http://example.com/images/cookbook_basics.jpg', NOW(), NOW());

-- Insert sample data for product_attributes
INSERT INTO product_attributes (product_id, attribute_name, attribute_value) VALUES
(1, 'Processor', 'Intel i7'),
(1, 'RAM', '16GB'),
(1, 'Storage', '512GB SSD'),
(2, 'Author', 'Jane Doe'),
(2, 'Genre', 'Fiction'),
(3, 'Size', 'M'),
(3, 'Color', 'Blue'),
(4, 'Screen Size', '6.7 inch'),
(4, 'Camera', '48MP');