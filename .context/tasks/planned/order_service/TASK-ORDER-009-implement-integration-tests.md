---
title: Triển khai Kiểm thử Tích hợp cho Dịch vụ Đặt hàng
type: task
status: planned
created: 2025-07-24T03:21:16
updated: 2025-07-24T03:21:16
id: TASK-ORDER-009
priority: high
memory_types: [procedural]
dependencies: [TASK-ORDER-003, TASK-ORDER-004, TASK-ORDER-005, TASK-ORDER-006]
tags: [testing, integration-test, order-service]
---

## Mô tả

Viết các bài kiểm thử tích hợp để xác minh các endpoint API và luồng nghiệp vụ end-to-end cho Dịch vụ Đặt hàng. Điều này đảm bảo rằng Dịch vụ Đặt hàng tích hợp đúng cách với các phụ thuộc của nó (cơ sở dữ liệu, Inventory Service, Payment Service, Message Queue).

## Mục tiêu

*   Xác minh chức năng của các endpoint API tạo, truy xuất và hủy đơn hàng với cơ sở dữ liệu thực.
*   Xác nhận luồng tạo đơn hàng tích hợp với Inventory Service (Soft Reservation).
*   Xác nhận luồng cập nhật trạng thái đơn hàng tích hợp với Payment Service (callback).
*   Đảm bảo việc xuất bản và tiêu thụ sự kiện qua Message Queue hoạt động chính xác.

## Danh sách kiểm tra

### Kiểm thử Tích hợp API
- [ ] **Kiểm tra endpoint `POST /api/orders`:**
    - **Ghi chú:** Sử dụng `@SpringBootTest` với `WebEnvironment.RANDOM_PORT` và `TestRestTemplate` hoặc `WebTestClient`. Thực hiện yêu cầu tạo đơn hàng và xác minh đơn hàng được lưu đúng cách, Soft Reservation được gọi, và trạng thái ban đầu chính xác.
    - **Vị trí:** `src/test/java/com/ecommerce/orderservice/integration/OrderApiIntegrationTest.java` (hoặc tương tự).
    - **Thực hành tốt nhất:** Sử dụng Testcontainers để khởi tạo cơ sở dữ liệu và các dịch vụ phụ thuộc (ví dụ: Inventory Service, Payment Service, Kafka) trong môi trường kiểm thử.
- [ ] **Kiểm tra endpoint `GET /api/orders/{id}`:**
    - **Ghi chú:** Xác minh việc truy xuất chi tiết đơn hàng, bao gồm các mặt hàng trong đơn hàng.
- [ ] **Kiểm tra endpoint `PUT /api/orders/{id}/cancel`:**
    - **Ghi chú:** Xác minh khả năng hủy đơn hàng và các cập nhật trạng thái liên quan. Test các trường hợp không thể hủy.

### Kiểm thử Tích hợp Luồng Nghiệp vụ
- [ ] **Kiểm tra luồng tạo đơn hàng hoàn chỉnh (bao gồm thanh toán):**
    - **Ghi chú:** Mô phỏng một giao dịch tạo đơn hàng từ đầu đến cuối, bao gồm phản hồi thanh toán thành công/thất bại và xác minh trạng thái đơn hàng cuối cùng.
    - **Thực hành tốt nhất:** Sử dụng các dịch vụ giả lập (mock services) cho Inventory và Payment nếu không sử dụng Testcontainers cho chúng.
- [ ] **Kiểm tra việc xuất bản sự kiện `order.created` và `order.payment.failed`:**
    - **Ghi chú:** Xác minh rằng các sự kiện này được gửi lên Kafka sau các hành động tương ứng. Sử dụng `EmbeddedKafkaBroker` hoặc Kafka test utilities để kiểm tra các sự kiện đã xuất bản.
- [ ] **Kiểm tra việc tiêu thụ sự kiện (`RefundSucceeded`, `RefundFailed`):**
    - **Ghi chú:** Mô phỏng việc gửi các sự kiện này vào Kafka và xác minh Dịch vụ Đặt hàng cập nhật trạng thái đơn hàng đúng cách.

## Tiến độ

*   **Kiểm thử Tích hợp API:** [ ]
*   **Kiểm thử Tích hợp Luồng Nghiệp vụ:** [ ]

## Phụ thuộc

*   `TASK-ORDER-003-implement-order-creation-api-soft-reservation.md`: API tạo đơn hàng.
*   `TASK-ORDER-004-implement-order-retrieval-cancellation-apis.md`: API truy xuất và hủy đơn hàng.
*   `TASK-ORDER-005-implement-payment-processing-integration.md`: Tích hợp thanh toán.
*   `TASK-ORDER-006-implement-event-publishing-consumption.md`: Xuất bản và tiêu thụ sự kiện.

## Các cân nhắc chính

*   **Môi trường kiểm thử:** Sử dụng môi trường kiểm thử nhất quán và cô lập (ví dụ: Docker Compose cho cục bộ, Testcontainers cho CI) là rất quan trọng cho các bài kiểm thử tích hợp đáng tin cậy.
*   **Tính chất bất đồng bộ:** Cần các cơ chế để đợi các sự kiện và cập nhật bất đồng bộ hoàn tất trước khi xác nhận (ví dụ: `Awaitility`).
*   **Dữ liệu kiểm thử:** Đảm bảo dữ liệu kiểm thử được thiết lập và dọn dẹp đúng cách để tránh sự can thiệp giữa các bài kiểm thử.

## Ghi chú

*   Kiểm thử tích hợp thường chậm hơn kiểm thử đơn vị, tập trung vào các luồng nghiệp vụ quan trọng.
*   Sử dụng `@ActiveProfiles("test")` để tải các cấu hình kiểm thử.

## Thảo luận

Thảo luận về cách tốt nhất để mô phỏng các dịch vụ bên ngoài (Inventory, Payment) trong kiểm thử tích hợp, hoặc nếu có thể sử dụng Testcontainers cho tất cả.

## Các bước tiếp theo

Tiếp tục với `TASK-ORDER-010-address-open-questions-performance.md` để hoàn thiện các cân nhắc kiến trúc.

## Trạng thái hiện tại

Đã lên kế hoạch.