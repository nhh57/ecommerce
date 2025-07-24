---
title: Triển khai Tích hợp Xử lý Thanh toán
type: task
status: completed
created: 2025-07-24T03:21:16
updated: 2025-07-24T08:18:20
id: TASK-ORDER-005
priority: high
memory_types: [procedural]
dependencies: [TASK-ORDER-001, TASK-ORDER-002, TASK-ORDER-003]
tags: [payment, integration, order-service]
---

## Mô tả

Triển khai tích hợp với Dịch vụ Thanh toán (Payment Service) để xử lý các yêu cầu thanh toán và nhận các callback kết quả thanh toán. Nhiệm vụ này bao gồm việc cập nhật trạng thái đơn hàng dựa trên kết quả thanh toán.

## Mục tiêu

*   Tạo client để gọi Dịch vụ Thanh toán.
*   Gửi yêu cầu thanh toán đến Dịch vụ Thanh toán sau khi đơn hàng được tạo và giữ chỗ tồn kho thành công.
*   Triển khai endpoint nội bộ để nhận callback kết quả thanh toán từ Dịch vụ Thanh toán.
*   Cập nhật trạng thái đơn hàng thành "Paid" hoặc "Payment Failed" dựa trên kết quả.

## Danh sách kiểm tra

### Tích hợp với Payment Service
- [x] **Tạo client cho Payment Service:**
    - **Ghi chú:** Sử dụng Feign Client, WebClient, hoặc RestTemplate để giao tiếp với Payment Service.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/client/PaymentServiceClient.java` (hoặc tương tự).
    - **Thực hành tốt nhất:** Định nghĩa một interface client rõ ràng với các phương thức tương ứng với API của Payment Service.
    - **Lỗi thường gặp:** Cấu hình URL không chính xác, lỗi serialization/deserialization.
- [x] **Gửi yêu cầu thanh toán:**
    - **Ghi chú:** Trong `OrderService` (sau khi Soft Reservation thành công), gọi phương thức `processPayment()` của PaymentServiceClient.
    - **Thực hành tốt nhất:** Truyền đủ thông tin đơn hàng và người dùng cho Payment Service.
    - **Lỗi thường gặp:** Không xử lý các ngoại lệ từ Payment Service.

### Xử lý Callback Thanh toán
- [x] **Tạo endpoint `payment-callback` trong `OrderController`:**
    - **Ghi chú:** Chú thích với `@PostMapping("/api/orders/{id}/payment-callback")`. Endpoint này sẽ nhận kết quả thanh toán từ Payment Service.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/controller/OrderController.java`.
    - **Thực hành tốt nhất:** Bảo vệ endpoint này bằng các biện pháp bảo mật thích hợp (ví dụ: IP whitelist, shared secret) vì nó là callback từ hệ thống bên ngoài.
- [x] **Triển khai logic xử lý callback trong `OrderService`:**
    - **Ghi chú:**
        *   Nhận kết quả thanh toán (thành công/thất bại).
        *   Cập nhật trạng thái đơn hàng (`Status`) trong `Order DB` thành "Paid" hoặc "Payment Failed".
        *   Ghi lại lịch sử trạng thái vào `OrderStatusHistory`.
    - **Thực hành tốt nhất:** Đảm bảo tính bất biến của việc cập nhật trạng thái (nếu callback được gọi nhiều lần).

## Tiến độ

*   **Tích hợp với Payment Service:** [x]
*   **Xử lý Callback Thanh toán:** [x]

## Phụ thuộc

*   `TASK-ORDER-001-setup-project-structure.md`: Cấu trúc dự án cơ bản.
*   `TASK-ORDER-002-implement-data-model-repository.md`: Các thực thể và repository đơn hàng.
*   `TASK-ORDER-003-implement-order-creation-api-soft-reservation.md`: API tạo đơn hàng.

## Các cân nhắc chính

*   **Độ tin cậy của Callback:** Payment Gateway có thể gửi callback nhiều lần hoặc không gửi. Cần cơ chế xử lý bất biến (idempotency) và cơ chế đối soát (reconciliation) để đảm bảo trạng thái cuối cùng chính xác.
*   **Bảo mật Callback:** Endpoint nhận callback phải được bảo vệ nghiêm ngặt để tránh giả mạo.
*   **Xử lý lỗi:** Làm thế nào để xử lý các lỗi khi cập nhật trạng thái đơn hàng sau callback?

## Ghi chú

*   Tập trung vào luồng thành công trước, sau đó mở rộng để xử lý các trường hợp thất bại.
*   Sử dụng các DTOs rõ ràng cho giao tiếp với Payment Service.

## Thảo luận

Thảo luận về phương thức giao tiếp ưu tiên với Payment Service (REST sync, event-driven, Feign Client). Xác định các biện pháp bảo mật cụ thể cho endpoint callback.

## Các bước tiếp theo

Tiếp tục với `TASK-ORDER-006-implement-event-publishing-consumption.md` để xử lý các sự kiện.

## Trạng thái hiện tại

Đã lên kế hoạch.