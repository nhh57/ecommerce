---
title: Triển khai Xuất bản Sự kiện Cập nhật Tồn kho
type: task
status: completed
created: 2025-07-24T03:29:29
updated: 2025-07-24T09:06:10
id: TASK-INV-006
priority: high
memory_types: [procedural]
dependencies: [TASK-INV-001, TASK-INV-002]
tags: [event-driven, kafka, publisher, inventory-service]
---

## Mô tả

Triển khai logic để xuất bản các sự kiện `inventory.updated` lên Kafka mỗi khi trạng thái tồn kho của một sản phẩm thay đổi. Nhiệm vụ này đảm bảo rằng các dịch vụ khác (ví dụ: Product Service) được thông báo về sự thay đổi tồn kho để duy trì tính nhất quán dữ liệu.

## Mục tiêu

*   Cấu hình Kafka Producer trong Dịch vụ Kho hàng.
*   Xuất bản sự kiện `inventory.updated` sau khi có bất kỳ thay đổi nào về `AvailableQuantity` hoặc `ReservedQuantity` của một sản phẩm.
*   Đảm bảo sự kiện chứa đầy đủ thông tin cần thiết cho các consumer.

## Danh sách kiểm tra

### Cấu hình Kafka Producer
- [x] **Cấu hình Kafka Producer:**
    - **Ghi chú:** Thêm các thuộc tính producer Kafka vào `application.properties`/`application.yml` (ví dụ: `spring.kafka.producer.bootstrap-servers`, `key-serializer`, `value-serializer`, `acks`).
    - **Vị trí:** `src/main/resources/application.properties` hoặc `application.yml`.
    - **Thực hành tốt nhất:** Sử dụng `acks=all` và `enable.idempotence=true` để đảm bảo gửi tin nhắn bền vững và bất biến.
    - **Lỗi thường gặp:** Cấu hình serializer không chính xác.

### Logic Xuất bản Sự kiện
- [x] **Thêm phương thức `publishInventoryUpdateEvent()` vào `InventoryService`:**
    - **Ghi chú:** Phương thức này sẽ được gọi sau mỗi thao tác cập nhật tồn kho (Soft Reservation, Hard Reservation, Rollback).
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/service/InventoryService.java`.
    - **Thực hành tốt nhất:** Phương thức này nên nhận `productId` và `newQuantity` (hoặc toàn bộ `Inventory` entity) để tạo sự kiện.
- [x] **Tạo sự kiện `inventory.updated`:**
    - **Ghi chú:** Định nghĩa cấu trúc sự kiện (ví dụ: `{ "productId": ..., "newQuantity": ..., "oldQuantity": ..., "changeType": ... }`).
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/event/InventoryUpdatedEvent.java`.
- [x] **Sử dụng `KafkaTemplate` để gửi sự kiện:**
    - **Ghi chú:** Tiêm `KafkaTemplate<String, InventoryUpdatedEvent>` vào `InventoryService` và sử dụng nó để gửi sự kiện đến topic `inventory.updated`.
    - **Thực hành tốt nhất:** Sử dụng `productId` làm key của tin nhắn để đảm bảo thứ tự tin nhắn cho cùng một sản phẩm.

## Tiến độ

*   **Cấu hình Kafka Producer:** [x]
*   **Logic Xuất bản Sự kiện:** [x]

## Phụ thuộc

*   `TASK-INV-001-setup-project-structure.md`: Thiết lập dự án và phụ thuộc Kafka client.
*   `TASK-INV-002-implement-inventory-data-model-repository.md`: Các thực thể kho hàng.

## Các cân nhắc chính

*   **Đảm bảo phân phối (Guaranteed Delivery):** Đảm bảo sự kiện được gửi thành công đến Kafka ngay cả khi có lỗi mạng tạm thời hoặc broker không khả dụng.
*   **Tính nhất quán:** Sự kiện `inventory.updated` phải được xuất bản sau khi thay đổi tồn kho đã được ghi vào cơ sở dữ liệu.
*   **Thông tin sự kiện:** Sự kiện nên chứa đủ thông tin để consumer có thể hiểu và xử lý (ví dụ: `productId`, số lượng mới, loại thay đổi).

## Ghi chú

*   Đảm bảo rằng việc xuất bản sự kiện được tích hợp vào các phương thức xử lý tồn kho chính (Soft Reserve, Hard Reserve, Rollback).
*   Xem xét sử dụng Transactional Outbox Pattern để đảm bảo sự kiện được xuất bản atomic với giao dịch cơ sở dữ liệu.

## Thảo luận

Thảo luận về định dạng chính xác của sự kiện `inventory.updated` và các trường cần thiết.

## Các bước tiếp theo

Tiếp tục với `TASK-INV-007-implement-inventory-caching.md` để triển khai caching.

## Trạng thái hiện tại

Đã lên kế hoạch.