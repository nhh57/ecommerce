---
title: Triển khai Consumer Sự kiện Hoàn trả Giữ chỗ Tồn kho Tạm thời (Rollback Soft Reservation)
type: task
status: planned
created: 2025-07-24T03:29:29
updated: 2025-07-24T03:29:29
id: TASK-INV-005
priority: high
memory_types: [procedural]
dependencies: [TASK-INV-001, TASK-INV-002]
tags: [event-driven, kafka, consumer, rollback, soft-reservation, inventory-service]
---

## Mô tả

Triển khai logic để tiêu thụ các sự kiện `order.payment.failed` từ Kafka và thực hiện hoàn trả tồn kho tạm thời (Rollback Soft Reservation) trong cơ sở dữ liệu. Nhiệm vụ này đảm bảo rằng tồn kho được trả lại khi đơn hàng không được thanh toán thành công.

## Mục tiêu

*   Cấu hình Kafka consumer properties trong Dịch vụ Kho hàng để lắng nghe sự kiện `order.payment.failed`.
*   Tạo một Kafka listener để đăng ký (subscribe) topic `order.payment.failed`.
*   Phân tích cú pháp thông báo sự kiện `order.payment.failed` đến để trích xuất ID sản phẩm và số lượng cần hoàn trả.
*   Hoàn trả tồn kho tạm thời trong `Inventory DB` và ghi log sự thay đổi.
*   Xuất bản sự kiện `inventory.updated` sau khi hoàn trả tồn kho tạm thời.

## Danh sách kiểm tra

### Kafka Consumer Setup
- [ ] **Cấu hình Kafka Listener:**
    - **Ghi chú:** Thêm các thuộc tính consumer Kafka vào `application.properties`/`application.yml` (ví dụ: `spring.kafka.consumer.bootstrap-servers`, `group-id`, `auto-offset-reset`).
    - **Vị trí:** `src/main/resources/application.properties` hoặc `application.yml`.
    - **Thực hành tốt nhất:** Sử dụng một nhóm consumer chuyên dụng cho Dịch vụ Kho hàng.
- [ ] **Tạo lớp Kafka Listener cho `order.payment.failed`:**
    - **Ghi chú:** Chú thích một phương thức với `@KafkaListener(topics = "order.payment.failed", groupId = "inventory-service-payment-failed-consumer")`.
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/listener/OrderPaymentFailedListener.java` (hoặc tương tự).
    - **Thực hành tốt nhất:** Giữ listener methods tập trung vào việc tiêu thụ và ủy quyền cho một lớp dịch vụ để xử lý.

### Logic Xử lý Rollback Soft Reservation
- [ ] **Định nghĩa cấu trúc thông báo sự kiện `order.payment.failed`:**
    - **Ghi chú:** Hiểu cấu trúc JSON/Avro/Protobuf mong đợi của sự kiện `order.payment.failed` (ví dụ: `{ "orderId": ..., "items": [{ "productId": ..., "quantity": ... }] }`).
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/event/OrderPaymentFailedEvent.java`.
- [ ] **Triển khai logic hoàn trả tồn kho tạm thời:**
    - **Ghi chú:** Trong phương thức Kafka listener, trích xuất `productId` và `quantity` từ sự kiện.
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/service/InventoryService.java` (thêm phương thức như `rollbackSoftReservation(Long productId, int quantity, Long orderId)`).
    - **Thực hành tốt nhất:**
        *   Sử dụng Optimistic Locking (`@Version`) khi cập nhật `Inventory` entity.
        *   Tăng `AvailableQuantity` và giảm `ReservedQuantity` trong `Inventory` entity.
        *   Tạo bản ghi `InventoryLog` cho thao tác Rollback Soft Reservation.
        *   Đảm bảo thao tác là bất biến (idempotent).
- [ ] **Xuất bản sự kiện `inventory.updated`:**
    - **Ghi chú:** Sau khi hoàn trả tồn kho tạm thời thành công, xuất bản một sự kiện `inventory.updated` với `productId` và `newQuantity`.
    - **Thực hành tốt nhất:** Đảm bảo sự kiện này được gửi đi để đồng bộ hóa các dịch vụ khác (ví dụ: Product Service).

## Tiến độ

*   **Kafka Consumer Setup:** [ ]
*   **Logic Xử lý Rollback Soft Reservation:** [ ]

## Phụ thuộc

*   `TASK-INV-001-setup-project-structure.md`: Thiết lập dự án và phụ thuộc Kafka client.
*   `TASK-INV-002-implement-inventory-data-model-repository.md`: Các thực thể và repository kho hàng.

## Các cân nhắc chính

*   **Tính bất biến (Idempotency):** Đảm bảo logic hoàn trả tồn kho tạm thời là bất biến để xử lý các thông báo trùng lặp từ Kafka.
*   **Xử lý lỗi và DLQ:** Triển khai xử lý lỗi mạnh mẽ và cơ chế Dead Letter Queue cho các thông báo không thể xử lý được.
*   **Thứ tự thông báo:** Đảm bảo thứ tự xử lý các sự kiện cho cùng một sản phẩm bằng cách phân vùng Kafka theo `productId`.

## Ghi chú

*   Tập trung vào logic hoàn trả và xuất bản sự kiện `inventory.updated`.
*   Đảm bảo rằng `ReservedQuantity` được giảm và `AvailableQuantity` được tăng khi thực hiện Rollback Soft Reservation.

## Thảo luận

Thảo luận về cách xử lý các trường hợp khi Rollback Soft Reservation thất bại (ví dụ: lỗi đồng thời hoặc dữ liệu không khớp). Có cần cơ chế bù trừ (compensation) nào không?

## Các bước tiếp theo

Tiếp tục với `TASK-INV-006-implement-inventory-update-event-publisher.md` để triển khai xuất bản sự kiện cập nhật tồn kho.

## Trạng thái hiện tại

Đã lên kế hoạch.