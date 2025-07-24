---
title: Triển khai Caching Đơn hàng
type: task
status: planned
created: 2025-07-24T03:21:16
updated: 2025-07-24T03:21:16
id: TASK-ORDER-007
priority: medium
memory_types: [procedural]
dependencies: [TASK-ORDER-003, TASK-ORDER-004]
tags: [caching, redis, performance, order-service]
---

## Mô tả

Tích hợp Redis để caching dữ liệu đơn hàng nhằm cải thiện hiệu suất đọc cho các đơn hàng gần đây của người dùng hoặc các đơn hàng "hot" được truy vấn thường xuyên.

## Mục tiêu

*   Cấu hình hạ tầng caching (ví dụ: Spring Cache với Redis).
*   Triển khai caching cho các phương thức truy xuất đơn hàng (ví dụ: `getOrderById`, `getOrdersByUserId`).
*   Định nghĩa các khóa cache và chiến lược thời gian sống (TTL).
*   Triển khai cơ chế vô hiệu hóa cache khi dữ liệu đơn hàng thay đổi.

## Danh sách kiểm tra

### Cấu hình Caching
- [ ] **Thêm phụ thuộc Caching:**
    - **Ghi chú:** Bao gồm `spring-boot-starter-data-redis` hoặc `spring-boot-starter-cache`.
    - **Vị trí:** `pom.xml` hoặc `build.gradle`.
    - **Thực hành tốt nhất:** Chọn giải pháp caching phù hợp.
    - **Lỗi thường gặp:** Thiếu phụ thuộc, phiên bản không chính xác.
- [ ] **Cấu hình Cache Manager:**
    - **Ghi chú:** Kích hoạt caching với `@EnableCaching` và cấu hình `RedisCacheManager`.
    - **Vị trí:** Lớp ứng dụng chính hoặc một lớp cấu hình chuyên dụng.
    - **Thực hành tốt nhất:** Tùy chỉnh tên cache, TTL và serialization.
    - **Lỗi thường gặp:** Chi tiết kết nối Redis không chính xác.

### Triển khai Caching
- [ ] **Áp dụng Caching cho các phương thức của `OrderService`:**
    - **Ghi chú:** Sử dụng chú thích `@Cacheable` trên các phương thức như `getOrderById(Long id)` và `getOrdersByUserId(Long userId)`.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/service/OrderService.java`.
    - **Thực hành tốt nhất:** Sử dụng SpEL cho các khóa cache động (ví dụ: `key="#id"`).
    - **Lỗi thường gặp:** Caching các đối tượng có thể thay đổi mà không sao chép phòng thủ, khóa cache không chính xác.
- [ ] **Định nghĩa Khóa Cache và TTL:**
    - **Ghi chú:**
        *   `order:{orderId}` cho chi tiết đơn hàng (ví dụ: TTL 15-30 phút).
        *   `user:{userId}:orders` cho danh sách đơn hàng của người dùng (ví dụ: TTL 5-10 phút).
    - **Thực hành tốt nhất:** TTL nên cân bằng giữa độ tươi mới của dữ liệu và lợi ích hiệu suất.
    - **Lỗi thường gặp:** TTL quá ngắn/dài.

### Vô hiệu hóa Cache
- [ ] **Triển khai vô hiệu hóa cache:**
    - **Ghi chú:** Sử dụng `@CacheEvict` trên các phương thức cập nhật/hủy đơn hàng (ví dụ: `createOrder`, `cancelOrder`, các phương thức cập nhật trạng thái) để vô hiệu hóa các mục cache liên quan.
    - **Thực hành tốt nhất:** Đảm bảo vô hiệu hóa cache hiệu quả khi trạng thái đơn hàng thay đổi.
    - **Lỗi thường gặp:** Không vô hiệu hóa cache sau khi dữ liệu thay đổi, dẫn đến dữ liệu cũ.

## Tiến độ

*   **Cấu hình Caching:** [ ]
*   **Triển khai Caching:** [ ]
*   **Vô hiệu hóa Cache:** [ ]

## Phụ thuộc

*   `TASK-ORDER-003-implement-order-creation-api-soft-reservation.md`: Các API tạo đơn hàng.
*   `TASK-ORDER-004-implement-order-retrieval-cancellation-apis.md`: Các API truy xuất và hủy đơn hàng.

## Các cân nhắc chính

*   **Tính nhất quán của Cache:** Đảm bảo dữ liệu được cache luôn nhất quán với cơ sở dữ liệu. Vô hiệu hóa dựa trên sự kiện là rất quan trọng.
*   **Serialization:** Cách các đối tượng được serialization/deserialization vào/từ cache.

## Ghi chú

*   Bắt đầu với việc caching các đơn hàng riêng lẻ và sau đó mở rộng sang danh sách.
*   Theo dõi tỷ lệ truy cập cache để đánh giá hiệu quả.

## Thảo luận

Thảo luận về các giá trị TTL cụ thể và chiến lược vô hiệu hóa cache.

## Các bước tiếp theo

Tiếp tục với `TASK-ORDER-008-implement-unit-tests.md` để đảm bảo chất lượng mã.

## Trạng thái hiện tại

Đã lên kế hoạch.