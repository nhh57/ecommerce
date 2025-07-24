---
title: Triển khai Kiểm thử Tích hợp cho Dịch vụ Kho hàng
type: task
status: planned
created: 2025-07-24T03:29:29
updated: 2025-07-24T03:29:29
id: TASK-INV-010
priority: high
memory_types: [procedural]
dependencies: [TASK-INV-003, TASK-INV-004, TASK-INV-005]
tags: [testing, integration-test, inventory-service]
---

## Mô tả

Viết các bài kiểm thử tích hợp để xác minh các endpoint API và luồng nghiệp vụ end-to-end cho Dịch vụ Kho hàng. Điều này đảm bảo rằng Dịch vụ Kho hàng tích hợp đúng cách với các phụ thuộc của nó (cơ sở dữ liệu, Redis, Message Queue).

## Mục tiêu

*   Xác minh chức năng của API Soft Reservation với cơ sở dữ liệu thực và Redis.
*   Xác nhận luồng Hard Reservation từ sự kiện `order.created`.
*   Xác nhận luồng Rollback Soft Reservation từ sự kiện `order.payment.failed`.
*   Đảm bảo việc xuất bản sự kiện `inventory.updated` hoạt động chính xác.
*   Kiểm tra tính đúng đắn của các cơ chế kiểm soát đồng thời.

## Danh sách kiểm tra

### Kiểm thử Tích hợp API
- [ ] **Kiểm tra endpoint `POST /api/inventory/soft-reserve`:**
    - **Ghi chú:** Sử dụng `@SpringBootTest` với `WebEnvironment.RANDOM_PORT` và `TestRestTemplate` hoặc `WebTestClient`. Thực hiện yêu cầu giữ chỗ và xác minh tồn kho trong DB và Redis được cập nhật đúng cách. Kiểm tra các trường hợp không đủ tồn kho.
    - **Vị trí:** `src/test/java/com/ecommerce/inventoryservice/integration/InventoryApiIntegrationTest.java` (hoặc tương tự).
    - **Thực hành tốt nhất:** Sử dụng Testcontainers để khởi tạo cơ sở dữ liệu và Redis trong môi trường kiểm thử.

### Kiểm thử Tích hợp Tiêu thụ Sự kiện
- [ ] **Kiểm tra tiêu thụ sự kiện `order.created` (Hard Reservation):**
    - **Ghi chú:** Sử dụng `EmbeddedKafkaBroker` hoặc Testcontainers với Kafka. Phát hành một thông báo `order.created` và xác minh rằng listener của Dịch vụ Kho hàng tiêu thụ nó và cập nhật tồn kho vĩnh viễn trong DB và Redis.
    - **Vị trí:** `src/test/java/com/ecommerce/inventoryservice/integration/OrderCreatedEventIntegrationTest.java`.
    - **Thực hành tốt nhất:** Đảm bảo bài kiểm thử đợi quá trình xử lý sự kiện bất đồng bộ hoàn tất trước khi xác nhận.
- [ ] **Kiểm tra tiêu thụ sự kiện `order.payment.failed` (Rollback Soft Reservation):**
    - **Ghi chú:** Tương tự như trên, phát hành một thông báo `order.payment.failed` và xác minh tồn kho tạm thời được hoàn trả.

### Kiểm thử Tích hợp Xuất bản Sự kiện
- [ ] **Kiểm tra xuất bản sự kiện `inventory.updated`:**
    - **Ghi chú:** Sau khi thực hiện Soft/Hard Reservation hoặc Rollback, xác minh rằng sự kiện `inventory.updated` được xuất bản lên Kafka với nội dung chính xác.
    - **Thực hành tốt nhất:** Sử dụng `EmbeddedKafkaBroker` hoặc Kafka test utilities để kiểm tra các sự kiện đã xuất bản.

### Kiểm thử Đồng thời
- [ ] **Kiểm tra các kịch bản đồng thời:**
    - **Ghi chú:** Viết các bài kiểm thử để mô phỏng nhiều yêu cầu đồng thời đến cùng một sản phẩm để kiểm tra Optimistic Locking và CAS trên Redis.
    - **Thực hành tốt nhất:** Sử dụng `CountDownLatch` hoặc các cơ chế đồng bộ hóa khác để kiểm soát luồng kiểm thử.

## Tiến độ

*   **Kiểm thử Tích hợp API:** [ ]
*   **Kiểm thử Tích hợp Tiêu thụ Sự kiện:** [ ]
*   **Kiểm thử Tích hợp Xuất bản Sự kiện:** [ ]
*   **Kiểm thử Đồng thời:** [ ]

## Phụ thuộc

*   `TASK-INV-003-implement-soft-reservation-api.md`: API Soft Reservation.
*   `TASK-INV-004-implement-hard-reservation-event-consumer.md`: Logic Hard Reservation.
*   `TASK-INV-005-implement-rollback-soft-reservation-event-consumer.md`: Logic Rollback Soft Reservation.

## Các cân nhắc chính

*   **Môi trường kiểm thử:** Sử dụng môi trường kiểm thử nhất quán và cô lập (ví dụ: Docker Compose cho cục bộ, Testcontainers cho CI) là rất quan trọng cho các bài kiểm thử tích hợp đáng tin cậy.
*   **Tính chất bất đồng bộ:** Cần các cơ chế để đợi các sự kiện và cập nhật bất đồng bộ hoàn tất trước khi xác nhận (ví dụ: `Awaitility`).
*   **Dữ liệu kiểm thử:** Đảm bảo dữ liệu kiểm thử được thiết lập và dọn dẹp đúng cách để tránh sự can thiệp giữa các bài kiểm thử.

## Ghi chú

*   Kiểm thử tích hợp thường chậm hơn kiểm thử đơn vị, tập trung vào các luồng nghiệp vụ quan trọng.
*   Sử dụng `@ActiveProfiles("test")` để tải các cấu hình kiểm thử.

## Thảo luận

Thảo luận về cách tốt nhất để mô phỏng các dịch vụ bên ngoài (Order Service) trong kiểm thử tích hợp, hoặc nếu có thể sử dụng Testcontainers cho tất cả.

## Các bước tiếp theo

Tiếp tục với `TASK-INV-011-address-open-questions-performance.md` để hoàn thiện các cân nhắc kiến trúc.

## Trạng thái hiện tại

Đã lên kế hoạch.