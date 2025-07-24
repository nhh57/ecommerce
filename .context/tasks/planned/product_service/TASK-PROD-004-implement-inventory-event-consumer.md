---
title: Triển khai Consumer Sự kiện Tồn kho
type: task
status: planned
created: 2025-07-24T03:00:34
updated: 2025-07-24T03:00:34
id: TASK-PROD-004
priority: high
memory_types: [procedural]
dependencies: [TASK-PROD-001, TASK-PROD-002]
tags: [event-driven, kafka, consumer, product-service]
---

## Mô tả

Triển khai logic để tiêu thụ các sự kiện `inventory.updated` từ Kafka và cập nhật `DisplayStatus` của sản phẩm trong cơ sở dữ liệu. Điều này đảm bảo rằng thông tin sản phẩm hiển thị cho người dùng nhất quán với trạng thái tồn kho thực tế.

## Mục tiêu

*   Cấu hình các thuộc tính consumer Kafka trong Dịch vụ Sản phẩm.
*   Tạo một Kafka listener để đăng ký (subscribe) topic `inventory.updated`.
*   Phân tích cú pháp thông báo sự kiện `inventory.updated` đến để trích xuất ID sản phẩm và số lượng mới có liên quan.
*   Cập nhật `DisplayStatus` của sản phẩm tương ứng trong `Product DB` dựa trên số lượng mới (ví dụ: "Còn hàng" hoặc "Hết hàng").

## Danh sách kiểm tra

### Thiết lập Consumer Kafka
- [ ] **Cấu hình Kafka Listener:**
    - **Ghi chú:** Thêm các thuộc tính consumer Kafka vào `application.properties`/`application.yml` (ví dụ: `spring.kafka.consumer.bootstrap-servers`, `group-id`, `auto-offset-reset`).
    - **Vị trí:** `src/main/resources/application.properties` hoặc `application.yml`.
    - **Thực hành tốt nhất:** Sử dụng một nhóm consumer chuyên dụng cho Dịch vụ Sản phẩm để cho phép mở rộng độc lập.
    - **Lỗi thường gặp:** Địa chỉ bootstrap server không chính xác, xung đột group ID.
- [ ] **Tạo lớp Kafka Listener:**
    - **Ghi chú:** Chú thích một phương thức với `@KafkaListener(topics = "inventory.updated", groupId = "product-service-group")`.
    - **Vị trí:** `src/main/java/com/ecommerce/productservice/listener/InventoryUpdateListener.java` (hoặc tương tự).
    - **Thực hành tốt nhất:** Giữ cho các phương thức listener tập trung vào việc tiêu thụ và ủy quyền cho một lớp dịch vụ để xử lý.
    - **Lỗi thường gặp:** Tên topic không chính xác, thiếu `@EnableKafka` trên lớp ứng dụng chính.

### Logic Xử lý Sự kiện
- [ ] **Định nghĩa cấu trúc thông báo sự kiện:**
    - **Ghi chú:** Hiểu cấu trúc JSON/Avro/Protobuf mong đợi của sự kiện `inventory.updated` (ví dụ: `{ "productId": 123, "newQuantity": 0 }`). Tạo một DTO/POJO tương ứng.
    - **Vị trí:** `src/main/java/com/ecommerce/productservice/event/InventoryUpdatedEvent.java`.
    - **Thực hành tốt nhất:** Sử dụng một schema registry (nếu có) cho sự tiến hóa schema.
    - **Lỗi thường gặp:** Schema không khớp dẫn đến lỗi deserialization.
- [ ] **Triển khai logic cập nhật trạng thái sản phẩm:**
    - **Ghi chú:** Trong phương thức Kafka listener, trích xuất `productId` và `newQuantity`. Sử dụng `ProductRepository` để tìm sản phẩm và cập nhật `DisplayStatus` của nó dựa trên `newQuantity` (ví dụ: nếu `newQuantity == 0`, đặt thành "Hết hàng"; ngược lại, "Còn hàng").
    - **Vị trí:** `src/main/java/com/ecommerce/productservice/service/ProductService.java` (thêm một phương thức như `updateProductDisplayStatus(Long productId, int newQuantity)`).
    - **Thực hành tốt nhất:** Đảm bảo thao tác cập nhật là bất biến (idempotent) để xử lý các thông báo trùng lặp. Sử dụng cập nhật giao dịch.
    - **Lỗi thường gặp:** Không xử lý `ProductNotFoundException`, điều kiện chạy (race conditions) nếu nhiều consumer cập nhật cùng một sản phẩm mà không có khóa thích hợp (mặc dù phân vùng Kafka giúp ích).

## Tiến độ

*   **Thiết lập Consumer Kafka:** [ ]
*   **Logic Xử lý Sự kiện:** [ ]

## Phụ thuộc

*   `TASK-PROD-001-setup-project-structure.md`: Thiết lập dự án và phụ thuộc Kafka client.
*   `TASK-PROD-002-implement-data-model-repository.md`: Các thực thể và repository sản phẩm.

## Các cân nhắc chính

*   **Tính bất biến (Idempotency):** Thao tác cập nhật phải là bất biến để ngăn chặn các vấn đề nếu cùng một thông báo được xử lý nhiều lần (ví dụ: do consumer rebalancing hoặc thử lại).
*   **Xử lý lỗi:** Triển khai xử lý lỗi mạnh mẽ cho các lỗi deserialization thông báo hoặc lỗi cập nhật cơ sở dữ liệu. Cân nhắc Dead Letter Queues (DLQs) cho các thông báo không thể xử lý được.
*   **Thứ tự thông báo:** Đảm bảo các sự kiện `inventory.updated` cho cùng một sản phẩm được xử lý theo thứ tự. Điều này thường dựa vào phân vùng Kafka theo `productId`.

## Ghi chú

*   Bắt đầu với một listener đơn giản và logic cập nhật cơ bản.
*   Cân nhắc sử dụng `ConcurrentKafkaListenerContainerFactory` của Spring for Kafka để xử lý đồng thời.

## Thảo luận

Định dạng chính xác của sự kiện `inventory.updated` nên được xác nhận với nhóm Dịch vụ Kho hàng. Chiến lược xử lý các thông báo không thể xử lý được (ví dụ: ID sản phẩm không hợp lệ, lỗi DB tạm thời) cần được xác định (thử lại, DLQ).

## Các bước tiếp theo

Tiếp tục với `TASK-PROD-005-implement-product-caching.md` để tối ưu hóa hiệu suất đọc.

## Trạng thái hiện tại

Đã lên kế hoạch.