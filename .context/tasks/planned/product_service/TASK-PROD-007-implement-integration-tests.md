---
title: Triển khai Kiểm thử Tích hợp cho Dịch vụ Sản phẩm
type: task
status: planned
created: 2025-07-24T03:00:34
updated: 2025-07-24T03:00:34
id: TASK-PROD-007
priority: high
memory_types: [procedural]
dependencies: [TASK-PROD-003, TASK-PROD-004]
tags: [testing, integration-test, product-service]
---

## Mô tả

Viết các bài kiểm thử tích hợp để xác minh các endpoint API và việc tiêu thụ sự kiện cho Dịch vụ Sản phẩm. Điều này đảm bảo rằng Dịch vụ Sản phẩm tích hợp đúng cách với các phụ thuộc của nó (cơ sở dữ liệu, Message Queue).

## Mục tiêu

*   Xác minh chức năng của các endpoint `GET /api/products` và `GET /api/products/{id}` với một cơ sở dữ liệu thực.
*   Xác nhận rằng Kafka listener tiêu thụ đúng các sự kiện `inventory.updated` và cập nhật trạng thái hiển thị sản phẩm trong cơ sở dữ liệu.
*   Đảm bảo giao tiếp và luồng dữ liệu phù hợp giữa Dịch vụ Sản phẩm và các phụ thuộc bên ngoài của nó.

## Danh sách kiểm tra

### Kiểm thử Tích hợp API
- [ ] **Kiểm tra endpoint `GET /api/products`:**
    - **Ghi chú:** Sử dụng `@SpringBootTest` của Spring Boot với `WebEnvironment.RANDOM_PORT` và `TestRestTemplate` hoặc `WebTestClient`. Chèn dữ liệu kiểm thử vào cơ sở dữ liệu và xác minh API trả về danh sách sản phẩm chính xác.
    - **Vị trí:** `src/test/java/com/ecommerce/productservice/integration/ProductApiIntegrationTest.java` (hoặc tương tự).
    - **Thực hành tốt nhất:** Sử dụng cơ sở dữ liệu kiểm thử chuyên dụng (ví dụ: Testcontainers với PostgreSQL/MySQL) cho các bài kiểm thử cô lập và có thể tái tạo.
    - **Lỗi thường gặp:** Tải ngữ cảnh không chính xác, các vấn đề kết nối cơ sở dữ liệu.
- [ ] **Kiểm tra endpoint `GET /api/products/{id}`:**
    - **Ghi chú:** Xác minh việc truy xuất một sản phẩm duy nhất theo ID. Kiểm tra cả các kịch bản thành công và "không tìm thấy".

### Kiểm thử Tích hợp Tiêu thụ Sự kiện
- [ ] **Kiểm tra việc tiêu thụ sự kiện `inventory.updated`:**
    - **Ghi chú:** Sử dụng `EmbeddedKafkaBroker` của Spring Kafka hoặc Testcontainers với Kafka để mô phỏng các sự kiện Kafka. Phát hành một thông báo `inventory.updated` kiểm thử và xác minh rằng listener của Dịch vụ Sản phẩm tiêu thụ nó và cập nhật cơ sở dữ liệu đúng cách.
    - **Vị trí:** `src/test/java/com/ecommerce/productservice/integration/InventoryEventIntegrationTest.java`.
    - **Thực hành tốt nhất:** Đảm bảo bài kiểm thử đợi quá trình xử lý sự kiện bất đồng bộ hoàn tất trước khi xác nhận.
    - **Lỗi thường gặp:** Listener không nhận được thông báo, cơ sở dữ liệu không được cập nhật như mong đợi.

## Tiến độ

*   **Kiểm thử Tích hợp API:** [ ]
*   **Kiểm thử Tích hợp Tiêu thụ Sự kiện:** [ ]

## Phụ thuộc

*   `TASK-PROD-003-implement-product-read-apis.md`: Yêu cầu các endpoint API đã triển khai.
*   `TASK-PROD-004-implement-inventory-event-consumer.md`: Yêu cầu logic consumer Kafka đã triển khai.

## Các cân nhắc chính

*   **Môi trường kiểm thử:** Sử dụng môi trường kiểm thử nhất quán và cô lập (ví dụ: Docker Compose cho phát triển cục bộ, Testcontainers cho CI) là rất quan trọng để có các bài kiểm thử tích hợp đáng tin cậy.
*   **Tính chất bất đồng bộ:** Lưu ý đến tính chất bất đồng bộ của giao tiếp hướng sự kiện. Sử dụng các cơ chế thích hợp (ví dụ: `Awaitility` hoặc `Thread.sleep` đơn giản cho các bài kiểm thử nhanh, nhưng ưu tiên `Awaitility`) để đợi các sự kiện được xử lý.
*   **Thiết lập/Xóa dữ liệu:** Đảm bảo dữ liệu kiểm thử được thiết lập đúng cách trước mỗi bài kiểm thử và được dọn dẹp sau đó để ngăn chặn sự can thiệp của kiểm thử.

## Ghi chú

*   Kiểm thử tích hợp thường chậm hơn kiểm thử đơn vị, vì vậy hãy giữ số lượng của chúng ở mức hợp lý và tập trung vào các điểm tích hợp quan trọng.
*   Cân nhắc sử dụng `@ActiveProfiles("test")` để tải các cấu hình dành riêng cho kiểm thử.

## Thảo luận

Thảo luận về cách tiếp cận thiết lập môi trường kiểm thử (ví dụ: dịch vụ nhúng so với dịch vụ bên ngoài) và các chiến lược quản lý dữ liệu kiểm thử cho kiểm thử tích hợp.

## Các bước tiếp theo

Tiếp tục với `TASK-PROD-008-address-open-questions-performance.md` để hoàn thiện các cân nhắc kiến trúc.

## Trạng thái hiện tại

Đã lên kế hoạch.