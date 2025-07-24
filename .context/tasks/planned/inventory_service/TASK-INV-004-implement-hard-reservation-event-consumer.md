---
title: Triển khai Consumer Sự kiện Trừ tồn kho Vĩnh viễn (Hard Reservation)
type: task
status: planned
created: 2025-07-24T03:29:29
updated: 2025-07-24T03:29:29
id: TASK-INV-004
priority: high
memory_types: [procedural]
dependencies: [TASK-INV-001, TASK-INV-002]
tags: [event-driven, kafka, consumer, hard-reservation, inventory-service]
---

## Mô tả

Triển khai logic để tiêu thụ các sự kiện `order.created` từ Kafka và thực hiện trừ tồn kho vĩnh viễn (Hard Reservation) trong cơ sở dữ liệu. Nhiệm vụ này đảm bảo rằng tồn kho được trừ chính thức sau khi đơn hàng được thanh toán thành công.

## Mục tiêu

*   Cấu hình Kafka consumer properties trong Dịch vụ Kho hàng để lắng nghe sự kiện `order.created`.
*   Tạo một Kafka listener để đăng ký (subscribe) topic `order.created`.
*   Phân tích cú pháp thông báo sự kiện `order.created` đến để trích xuất ID sản phẩm và số lượng cần trừ.
*   Trừ tồn kho vĩnh viễn trong `Inventory DB` và ghi log sự thay đổi.
*   Xuất bản sự kiện `inventory.updated` sau khi trừ tồn kho vĩnh viễn.

## Danh sách kiểm tra

### Kafka Consumer Setup
- [ ] **Cấu hình Kafka Listener:**
    - **Ghi chú:** Thêm các thuộc tính consumer Kafka vào `application.properties`/`application.yml` (ví dụ: `spring.kafka.consumer.bootstrap-servers`, `group-id`, `auto-offset-reset`).
    - **Vị trí:** `src/main/resources/application.properties` hoặc `application.yml`.
    - **Thực hành tốt nhất:** Sử dụng một nhóm consumer chuyên dụng cho Dịch vụ Kho hàng.
- [ ] **Tạo lớp Kafka Listener cho `order.created`:**
    - **Ghi chú:** Chú thích một phương thức với `@KafkaListener(topics = "order.created", groupId = "inventory-service-order-consumer")`.
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/listener/OrderCreatedListener.java` (hoặc tương tự).
    - **Thực hành tốt nhất:** Giữ listener methods tập trung vào việc tiêu thụ và ủy quyền cho một lớp dịch vụ để xử lý.

### Logic Xử lý Hard Reservation
- [ ] **Định nghĩa cấu trúc thông báo sự kiện `order.created`:**
    - **Ghi chú:** Hiểu cấu trúc JSON/Avro/Protobuf mong đợi của sự kiện `order.created` (ví dụ: `{ "orderId": ..., "items": [{ "productId": ..., "quantity": ... }] }`).
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/event/OrderCreatedEvent.java`.
- [ ] **Triển khai logic trừ tồn kho vĩnh viễn:**
    - **Ghi chú:** Trong phương thức Kafka listener, trích xuất `productId` và `quantity` từ sự kiện.
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/service/InventoryService.java` (thêm phương thức như `hardReserveInventory(Long productId, int quantity, Long orderId)`).
    - **Thực hành tốt nhất:**
        *   Sử dụng Optimistic Locking (`@Version`) khi cập nhật `Inventory` entity.
        *   Đảm bảo trừ `ReservedQuantity` và giảm `AvailableQuantity` trong `Inventory` entity.
        *   Tạo bản ghi `InventoryLog` cho thao tác Hard Reservation.
        *   Đảm bảo thao tác là bất biến (idempotent).
- [ ] **Xuất bản sự kiện `inventory.updated`:**
    - **Ghi chú:** Sau khi trừ tồn kho vĩnh viễn thành công, xuất bản một sự kiện `inventory.updated` với `productId` và `newQuantity`.
    - **Thực hành tốt nhất:** Đảm bảo sự kiện này được gửi đi để đồng bộ hóa các dịch vụ khác (ví dụ: Product Service).

## Tiến độ

*   **Kafka Consumer Setup:** [ ]
*   **Logic Xử lý Hard Reservation:** [ ]

## Phụ thuộc

*   `TASK-INV-001-setup-project-structure.md`: Thiết lập dự án và phụ thuộc Kafka client.
*   `TASK-INV-002-implement-inventory-data-model-repository.md`: Các thực thể và repository kho hàng.

## Các cân nhắc chính

*   **Tính bất biến (Idempotency):** Đảm bảo logic trừ tồn kho vĩnh viễn là bất biến để xử lý các thông báo trùng lặp từ Kafka.
*   **Xử lý lỗi và DLQ:** Triển khai xử lý lỗi mạnh mẽ và cơ chế Dead Letter Queue cho các thông báo không thể xử lý được.
*   **Thứ tự thông báo:** Đảm bảo thứ tự xử lý các sự kiện cho cùng một sản phẩm bằng cách phân vùng Kafka theo `productId`.

## Ghi chú

*   Tập trung vào logic trừ tồn kho vĩnh viễn và xuất bản sự kiện `inventory.updated`.
*   Đảm bảo rằng `ReservedQuantity` được giảm cùng với `AvailableQuantity` khi thực hiện Hard Reservation.

## Thảo luận

Thảo luận về cách xử lý các trường hợp khi Hard Reservation thất bại (ví dụ: do lỗi đồng thời hoặc dữ liệu không khớp). Có cần cơ chế bù trừ (compensation) nào không?

## Các bước tiếp theo

Tiếp tục với `TASK-INV-005-implement-rollback-soft-reservation-event-consumer.md` để xử lý hoàn trả tồn kho tạm thời.

## Trạng thái hiện tại

Đã lên kế hoạch.