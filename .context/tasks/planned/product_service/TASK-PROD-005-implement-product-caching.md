---
title: Triển khai Chiến lược Caching Sản phẩm
type: task
status: planned
created: 2025-07-24T03:00:34
updated: 2025-07-24T03:00:34
id: TASK-PROD-005
priority: medium
memory_types: [procedural]
dependencies: [TASK-PROD-003]
tags: [caching, redis, performance, product-service]
---

## Mô tả

Tích hợp Redis/Memcached để caching dữ liệu sản phẩm nhằm cải thiện hiệu suất đọc cho thông tin sản phẩm được truy cập thường xuyên. Nhiệm vụ này tập trung vào việc caching chi tiết và danh sách sản phẩm.

## Mục tiêu

*   Cấu hình hạ tầng caching (ví dụ: Spring Cache với Redis).
*   Triển khai caching cho các endpoint `GET /api/products` và `GET /api/products/{id}`.
*   Định nghĩa các khóa cache và chiến lược thời gian sống (TTL).
*   Triển khai các cơ chế vô hiệu hóa cache khi dữ liệu sản phẩm thay đổi (mặc dù Dịch vụ Sản phẩm chủ yếu chỉ đọc chi tiết sản phẩm, các cập nhật từ PIM sẽ kích hoạt vô hiệu hóa).

## Danh sách kiểm tra

### Cấu hình Caching
- [ ] **Thêm phụ thuộc Caching:**
    - **Ghi chú:** Bao gồm `spring-boot-starter-data-redis` (cho Redis) hoặc `spring-boot-starter-cache`.
    - **Vị trí:** `pom.xml` hoặc `build.gradle`.
    - **Thực hành tốt nhất:** Chọn giải pháp caching dựa trên nhu cầu dự án (Redis cho phân tán, Caffeine cho cục bộ).
    - **Lỗi thường gặp:** Thiếu phụ thuộc, phiên bản không chính xác.
- [ ] **Cấu hình Cache Manager:**
    - **Ghi chú:** Kích hoạt caching với `@EnableCaching` và cấu hình `CacheManager` (ví dụ: `RedisCacheManager`).
    - **Vị trí:** Lớp ứng dụng chính hoặc một lớp cấu hình chuyên dụng.
    - **Thực hành tốt nhất:** Tùy chỉnh tên cache, TTL và serialization.
    - **Lỗi thường gặp:** Chi tiết kết nối Redis không chính xác.

### Triển khai Caching
- [ ] **Áp dụng Caching cho các phương thức của `ProductService`:**
    - **Ghi chú:** Sử dụng chú thích `@Cacheable` của Spring trên các phương thức như `getAllProducts()` và `getProductById(Long id)` trong `ProductService`.
    - **Vị trí:** `src/main/java/com/ecommerce/productservice/service/ProductService.java`.
    - **Thực hành tốt nhất:** Sử dụng SpEL cho các khóa cache động (ví dụ: `@Cacheable(value="products", key="#id")`).
    - **Lỗi thường gặp:** Caching các đối tượng có thể thay đổi mà không sao chép phòng thủ, khóa cache không chính xác dẫn đến lỗi cache.
- [ ] **Định nghĩa Khóa Cache và TTL:**
    - **Ghi chú:**
        *   `product:{productId}` cho chi tiết sản phẩm riêng lẻ (ví dụ: TTL 1 giờ).
        *   `products:category:{categoryId}` cho danh sách sản phẩm theo danh mục (ví dụ: TTL 15-30 phút).
        *   `products:search:{query}` cho kết quả tìm kiếm (TTL ngắn hơn hoặc bị vô hiệu hóa khi tìm kiếm thay đổi).
    - **Thực hành tốt nhất:** TTL nên cân bằng giữa độ tươi mới của dữ liệu và lợi ích hiệu suất.
    - **Lỗi thường gặp:** TTL quá ngắn dẫn đến lỗi cache thường xuyên, TTL quá dài dẫn đến dữ liệu cũ.

### Vô hiệu hóa Cache (Cân nhắc)
- [ ] **Lập kế hoạch Vô hiệu hóa Cache:**
    - **Ghi chú:** Mặc dù Dịch vụ Sản phẩm là một consumer, nếu có hệ thống PIM (Quản lý Thông tin Sản phẩm) cập nhật chi tiết sản phẩm, nó sẽ cần kích hoạt vô hiệu hóa cache. Hiện tại, giả sử một cơ chế vô hiệu hóa dựa trên TTL đơn giản.
    - **Thực hành tốt nhất:** Sử dụng `@CacheEvict` hoặc các cuộc gọi quản lý cache rõ ràng. Đối với caching phân tán, hãy cân nhắc vô hiệu hóa dựa trên sự kiện.

## Tiến độ

*   **Cấu hình Caching:** [ ]
*   **Triển khai Caching:** [ ]
*   **Vô hiệu hóa Cache (Cân nhắc):** [ ]

## Phụ thuộc

*   `TASK-PROD-003-implement-product-read-apis.md`: Yêu cầu các API đọc hiện có để áp dụng caching.

## Các cân nhắc chính

*   **Tính nhất quán của Cache:** Làm thế nào để đảm bảo dữ liệu được cache luôn nhất quán với cơ sở dữ liệu, đặc biệt nếu chi tiết sản phẩm có thể được cập nhật bởi các hệ thống nội bộ khác (ví dụ: PIM). Đối với nhiệm vụ này, TTL đơn giản là đủ do tính chất đọc nhiều của Dịch vụ Sản phẩm.
*   **Serialization:** Cách các đối tượng được serialization/deserialization vào/từ cache. Sử dụng JSON hoặc định dạng nhị phân như Kryo để tăng hiệu quả.
*   **Tỷ lệ truy cập Cache (Cache Hit Ratio):** Theo dõi tỷ lệ truy cập cache để đánh giá hiệu quả của chiến lược caching.

## Ghi chú

*   Bắt đầu với việc caching từng sản phẩm và sau đó mở rộng sang danh sách/kết quả tìm kiếm.
*   Sử dụng cache cục bộ (như Caffeine) trước để triển khai đơn giản hơn, sau đó chuyển sang phân tán (Redis) nếu cần.

## Thảo luận

Lựa chọn giữa Redis và Memcached, cũng như các giá trị TTL cụ thể, nên được thảo luận với nhóm dựa trên các mẫu lưu lượng truy cập và yêu cầu về độ tươi mới của dữ liệu.

## Các bước tiếp theo

Tiếp tục với `TASK-PROD-006-implement-unit-tests.md` để đảm bảo chất lượng mã.

## Trạng thái hiện tại

Đã lên kế hoạch.