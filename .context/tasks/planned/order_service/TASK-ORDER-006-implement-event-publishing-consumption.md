---
title: Triển khai Xuất bản và Tiêu thụ Sự kiện
type: task
status: planned
created: 2025-07-24T03:21:16
updated: 2025-07-24T03:21:16
id: TASK-ORDER-006
priority: high
memory_types: [procedural]
dependencies: [TASK-ORDER-001, TASK-ORDER-002, TASK-ORDER-003, TASK-ORDER-005]
tags: [event-driven, kafka, rabbitmq, publisher, consumer, order-service]
---

## Mô tả

Triển khai logic để xuất bản các sự kiện quan trọng (`order.created`, `order.payment.failed`) lên Message Queue và tiêu thụ các sự kiện liên quan để cập nhật trạng thái đơn hàng (ví dụ: `RefundSucceeded`, `RefundFailed`).

## Mục tiêu

*   Cấu hình Kafka Producer và Consumer trong Dịch vụ Đặt hàng.
*   Xuất bản sự kiện `order.created` sau khi đơn hàng được thanh toán thành công.
*   Xuất bản sự kiện `order.payment.failed` khi thanh toán thất bại.
*   Cấu hình Kafka Consumer để tiêu thụ các sự kiện liên quan đến đơn hàng (ví dụ: kết quả hoàn tiền).
*   Cập nhật trạng thái đơn hàng dựa trên các sự kiện tiêu thụ.

## Danh sách kiểm tra

### Cấu hình Kafka
- [ ] **Cấu hình Kafka Producer:**
    - **Ghi chú:** Thêm các thuộc tính producer Kafka vào `application.properties`/`application.yml` (ví dụ: `spring.kafka.producer.bootstrap-servers`, `key-serializer`, `value-serializer`, `acks`).
    - **Vị trí:** `src/main/resources/application.properties` hoặc `application.yml`.
    - **Thực hành tốt nhất:** Sử dụng `acks=all` và `enable.idempotence=true` để đảm bảo gửi tin nhắn bền vững và bất biến.
    - **Lỗi thường gặp:** Cấu hình serializer không chính xác.
- [ ] **Cấu hình Kafka Consumer (cho các sự kiện liên quan đến đơn hàng):**
    - **Ghi chú:** Thêm các thuộc tính consumer Kafka (ví dụ: `group-id`, `auto-offset-reset`).
    - **Vị trí:** `src/main/resources/application.properties` hoặc `application.yml`.
    - **Thực hành tốt nhất:** Sử dụng một nhóm consumer chuyên dụng.

### Xuất bản Sự kiện
- [ ] **Xuất bản sự kiện `order.created`:**
    - **Ghi chú:** Gọi phương thức xuất bản sự kiện trong `OrderService` sau khi đơn hàng được cập nhật trạng thái "Paid".
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/service/OrderService.java`.
    - **Thực hành tốt nhất:** Đảm bảo sự kiện chứa đầy đủ thông tin cần thiết cho các consumer khác (ví dụ: `orderId`, `userId`, `totalAmount`, `items`).
- [ ] **Xuất bản sự kiện `order.payment.failed`:**
    - **Ghi chú:** Gọi phương thức xuất bản sự kiện trong `OrderService` khi thanh toán thất bại.
    - **Thực hành tốt nhất:** Sự kiện này nên chứa `orderId` và lý do thất bại.

### Tiêu thụ Sự kiện
- [ ] **Tạo Kafka Listener cho các sự kiện liên quan đến đơn hàng:**
    - **Ghi chú:** Ví dụ, một listener cho `order.refund.succeeded` và `order.refund.failed` từ Refund Service.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/listener/OrderEventListener.java`.
    - **Thực hành tốt nhất:** Xử lý các sự kiện bất biến và cập nhật trạng thái đơn hàng trong `Order DB`.
    - **Lỗi thường gặp:** Không xử lý lỗi deserialization, không cập nhật trạng thái đúng cách.

## Tiến độ

*   **Cấu hình Kafka:** [ ]
*   **Xuất bản Sự kiện:** [ ]
*   **Tiêu thụ Sự kiện:** [ ]

## Phụ thuộc

*   `TASK-ORDER-001-setup-project-structure.md`: Thiết lập dự án và các phụ thuộc Kafka.
*   `TASK-ORDER-002-implement-data-model-repository.md`: Các thực thể và repository đơn hàng.
*   `TASK-ORDER-003-implement-order-creation-api-soft-reservation.md`: Logic tạo đơn hàng.
*   `TASK-ORDER-005-implement-payment-processing-integration.md`: Logic xử lý thanh toán.

## Các cân nhắc chính

*   **Đảm bảo phân phối (Guaranteed Delivery):** Sử dụng cấu hình Kafka producer thích hợp (`acks=all`, `retries`) và cơ chế xử lý bất biến ở consumer để đảm bảo tin nhắn không bị mất hoặc xử lý trùng lặp.
*   **Xử lý lỗi và DLQ:** Triển khai cơ chế Dead Letter Queue (DLQ) cho các tin nhắn không thể xử lý được.
*   **Schema Evolution:** Sử dụng Schema Registry (nếu có) để quản lý phiên bản schema của các sự kiện.
*   **Tính nhất quán cuối cùng:** Dịch vụ Đặt hàng sẽ đạt được tính nhất quán cuối cùng với các dịch vụ khác thông qua việc xuất bản và tiêu thụ sự kiện.

## Ghi chú

*   Bắt đầu với việc xuất bản các sự kiện cơ bản và sau đó thêm các consumer khi cần.
*   Đảm bảo các sự kiện được xuất bản là atomic với giao dịch cơ sở dữ liệu (ví dụ: sử dụng Transactional Outbox Pattern).

## Thảo luận

Thảo luận về định dạng chính xác của các sự kiện (JSON, Avro, Protobuf) và cách quản lý schema.

## Các bước tiếp theo

Tiếp tục với `TASK-ORDER-007-implement-order-caching.md` để tối ưu hóa hiệu suất đọc.

## Trạng thái hiện tại

Đã lên kế hoạch.