---
title: Triển khai Unit Test cho Dịch vụ Sản phẩm
type: task
status: planned
created: 2025-07-24T03:00:34
updated: 2025-07-24T03:00:34
id: TASK-PROD-006
priority: high
memory_types: [procedural]
dependencies: [TASK-PROD-002, TASK-PROD-003, TASK-PROD-004]
tags: [testing, unit-test, product-service]
---

## Mô tả

Viết các bài kiểm tra đơn vị toàn diện cho logic cốt lõi và các phương thức repository trong Dịch vụ Sản phẩm. Điều này đảm bảo rằng các thành phần riêng lẻ của dịch vụ hoạt động chính xác một cách độc lập.

## Mục tiêu

*   Đạt được độ bao phủ mã cao cho logic nghiệp vụ của `ProductService`.
*   Xác minh hành vi chính xác của các phương thức `ProductRepository`.
*   Đảm bảo việc tiêu thụ sự kiện Kafka và logic cập nhật trạng thái sản phẩm hoạt động như mong đợi.

## Danh sách kiểm tra

### Unit Test cho `ProductService`
- [ ] **Kiểm tra `getAllProducts()`:**
    - **Ghi chú:** Xác minh rằng phương thức truy xuất sản phẩm chính xác từ repository và ánh xạ chúng tới DTOs. Các trường hợp kiểm tra cho danh sách trống, một sản phẩm, nhiều sản phẩm.
    - **Vị trí:** `src/test/java/com/ecommerce/productservice/service/ProductServiceTest.java`.
    - **Thực hành tốt nhất:** Sử dụng Mockito để giả lập `ProductRepository`.
    - **Lỗi thường gặp:** Không giả lập các phụ thuộc chính xác, kiểm tra chi tiết triển khai thay vì hành vi.
- [ ] **Kiểm tra `getProductById(Long id)`:**
    - **Ghi chú:** Các trường hợp kiểm tra cho sản phẩm được tìm thấy, sản phẩm không tìm thấy (mong đợi `ProductNotFoundException` hoặc tương tự).
- [ ] **Kiểm tra `updateProductDisplayStatus(Long productId, int newQuantity)`:**
    - **Ghi chú:** Xác minh rằng `DisplayStatus` được cập nhật chính xác dựa trên `newQuantity`. Kiểm tra các điều kiện biên (số lượng 0, số lượng > 0).
    - **Thực hành tốt nhất:** Đảm bảo tính bất biến được kiểm tra ngầm nếu logic cập nhật dựa vào nó.

### Unit Test cho Repositories
- [ ] **Kiểm tra `ProductRepository`:**
    - **Ghi chú:** Sử dụng `@DataJpaTest` của Spring Boot để kiểm tra các phương thức repository. Kiểm tra các thao tác CRUD cơ bản và các truy vấn tùy chỉnh (nếu có).
    - **Vị trí:** `src/test/java/com/ecommerce/productservice/repository/ProductRepositoryTest.java`.
    - **Thực hành tốt nhất:** Sử dụng cơ sở dữ liệu trong bộ nhớ (ví dụ: H2) để kiểm tra nhanh.
    - **Lỗi thường gặp:** Quên `@Transactional` có thể dẫn đến rò rỉ dữ liệu giữa các bài kiểm tra.

### Unit Test cho Kafka Listener
- [ ] **Kiểm tra `InventoryUpdateListener`:**
    - **Ghi chú:** Kiểm tra rằng listener nhận và ủy quyền sự kiện `inventory.updated` cho `ProductService` một cách chính xác.
    - **Vị trí:** `src/test/java/com/ecommerce/productservice/listener/InventoryUpdateListenerTest.java`.
    - **Thực hành tốt nhất:** Sử dụng Mockito để giả lập `ProductService`. Tránh khởi động một Kafka broker thực sự cho các bài kiểm tra đơn vị.
    - **Lỗi thường gặp:** Không kiểm tra deserialization của thông báo Kafka.

## Tiến độ

*   **Unit Test cho `ProductService`:** [ ]
*   **Unit Test cho Repositories:** [ ]
*   **Unit Test cho Kafka Listener:** [ ]

## Phụ thuộc

*   `TASK-PROD-002-implement-data-model-repository.md`: Các thực thể và repository phải được triển khai.
*   `TASK-PROD-003-implement-product-read-apis.md`: Các phương thức dịch vụ để truy xuất sản phẩm.
*   `TASK-PROD-004-implement-inventory-event-consumer.md`: Listener Kafka và logic cập nhật.

## Các cân nhắc chính

*   **Độ bao phủ kiểm tra:** Hướng tới độ bao phủ câu lệnh và nhánh cao, nhưng ưu tiên kiểm tra các đường dẫn quan trọng và logic phức tạp.
*   **Dữ liệu kiểm tra:** Sử dụng dữ liệu kiểm tra thực tế nhưng cô lập cho từng trường hợp kiểm tra.
*   **Giả lập (Mocking):** Hiểu khi nào nên sử dụng đối tượng thật so với đối tượng giả lập. Giả lập các phụ thuộc bên ngoài (ví dụ: Kafka, dịch vụ bên ngoài) và lớp cơ sở dữ liệu.

## Ghi chú

*   Tuân theo mẫu Arrange-Act-Assert (AAA) cho cấu trúc kiểm tra.
*   Cung cấp tên phương thức kiểm tra rõ ràng và mô tả.
*   Chạy kiểm tra thường xuyên trong quá trình phát triển.

## Thảo luận

Thảo luận về framework kiểm tra ưa thích (JUnit 5, Mockito) và bất kỳ mẫu kiểm tra cụ thể nào (ví dụ: kiểu BDD với Given-When-Then). Xác định ngưỡng bao phủ mã tối thiểu.

## Các bước tiếp theo

Tiếp tục với `TASK-PROD-007-implement-integration-tests.md` để xác minh các luồng end-to-end.

## Trạng thái hiện tại

Đã lên kế hoạch.