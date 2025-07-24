---
title: Triển khai Unit Test cho Dịch vụ Đặt hàng
type: task
status: planned
created: 2025-07-24T03:21:16
updated: 2025-07-24T03:21:16
id: TASK-ORDER-008
priority: high
memory_types: [procedural]
dependencies: [TASK-ORDER-002, TASK-ORDER-003, TASK-ORDER-004, TASK-ORDER-005, TASK-ORDER-006]
tags: [testing, unit-test, order-service]
---

## Mô tả

Viết các bài kiểm tra đơn vị toàn diện cho logic cốt lõi, các phương thức repository, logic xử lý API và event trong Dịch vụ Đặt hàng. Điều này đảm bảo rằng các thành phần riêng lẻ của dịch vụ hoạt động chính xác một cách độc lập.

## Mục tiêu

*   Đạt được độ bao phủ mã cao cho logic nghiệp vụ của `OrderService`.
*   Xác minh hành vi chính xác của các phương thức `OrderRepository`.
*   Đảm bảo logic tạo đơn hàng, truy xuất, hủy và tích hợp thanh toán hoạt động như mong đợi.
*   Đảm bảo việc xuất bản và tiêu thụ sự kiện hoạt động chính xác.

## Danh sách kiểm tra

### Unit Test cho `OrderService`
- [ ] **Kiểm tra `createOrder()`:**
    - **Ghi chú:** Xác minh rằng phương thức xử lý thành công việc tạo đơn hàng, gọi đúng Inventory Service, và lưu đơn hàng với trạng thái chính xác. Kiểm tra các trường hợp thất bại (ví dụ: Soft Reservation thất bại).
    - **Vị trí:** `src/test/java/com/ecommerce/orderservice/service/OrderServiceTest.java`.
    - **Thực hành tốt nhất:** Sử dụng Mockito để giả lập `OrderRepository`, `InventoryServiceClient`, `PaymentServiceClient`, và Kafka Producer.
- [ ] **Kiểm tra `getOrderById()` và `cancelOrder()`:**
    - **Ghi chú:** Test các trường hợp tìm thấy/không tìm thấy đơn hàng. Test logic hủy đơn hàng, bao gồm xác thực trạng thái.

### Unit Test cho Repositories
- [ ] **Kiểm tra `OrderRepository`, `OrderItemRepository`, `OrderStatusHistoryRepository`:**
    - **Ghi chú:** Sử dụng `@DataJpaTest` để kiểm tra các phương thức repository. Test các thao tác CRUD cơ bản và các truy vấn tùy chỉnh (nếu có).
    - **Vị trí:** `src/test/java/com/ecommerce/orderservice/repository/OrderRepositoryTest.java`.
    - **Thực hành tốt nhất:** Sử dụng cơ sở dữ liệu trong bộ nhớ (ví dụ: H2) cho các bài kiểm tra nhanh.

### Unit Test cho Controllers
- [ ] **Kiểm tra `OrderController`:**
    - **Ghi chú:** Sử dụng `@WebMvcTest` hoặc `@SpringBootTest` với MockMvc để kiểm tra các endpoint API.
    - **Vị trí:** `src/test/java/com/ecommerce/orderservice/controller/OrderControllerTest.java`.
    - **Thực hành tốt nhất:** Tập trung vào việc kiểm tra ánh xạ yêu cầu, xác thực đầu vào và phản hồi API.

### Unit Test cho Event Handling
- [ ] **Kiểm tra Kafka Producer:**
    - **Ghi chú:** Xác minh rằng các sự kiện `order.created` và `order.payment.failed` được gửi đi với nội dung chính xác.
    - **Thực hành tốt nhất:** Sử dụng `KafkaTemplate` giả lập hoặc `EmbeddedKafkaBroker` (nếu cần một môi trường nhẹ).
- [ ] **Kiểm tra Kafka Consumer (cho các sự kiện đến):**
    - **Ghi chú:** Kiểm tra rằng listener xử lý đúng các sự kiện đến (ví dụ: `RefundSucceeded`) và cập nhật trạng thái đơn hàng.

## Tiến độ

*   **Unit Test cho `OrderService`:** [ ]
*   **Unit Test cho Repositories:** [ ]
*   **Unit Test cho Controllers:** [ ]
*   **Unit Test cho Event Handling:** [ ]

## Phụ thuộc

*   `TASK-ORDER-002-implement-data-model-repository.md`: Các thực thể và repository phải được triển khai.
*   `TASK-ORDER-003-implement-order-creation-api-soft-reservation.md`: Logic tạo đơn hàng.
*   `TASK-ORDER-004-implement-order-retrieval-cancellation-apis.md`: Logic truy xuất và hủy đơn hàng.
*   `TASK-ORDER-005-implement-payment-processing-integration.md`: Logic tích hợp thanh toán.
*   `TASK-ORDER-006-implement-event-publishing-consumption.md`: Logic xuất bản và tiêu thụ sự kiện.

## Các cân nhắc chính

*   **Test Coverage:** Ưu tiên kiểm tra các đường dẫn quan trọng và logic phức tạp.
*   **Test Data:** Sử dụng dữ liệu kiểm tra thực tế nhưng cô lập.
*   **Mocking:** Giả lập các phụ thuộc bên ngoài (Inventory Service, Payment Service, Kafka).

## Ghi chú

*   Tuân theo mẫu Arrange-Act-Assert (AAA) cho cấu trúc kiểm tra.
*   Cung cấp tên phương thức kiểm tra rõ ràng và mô tả.

## Thảo luận

Thảo luận về các trường hợp biên và kịch bản lỗi cần được kiểm tra kỹ lưỡng.

## Các bước tiếp theo

Tiếp tục với `TASK-ORDER-009-implement-integration-tests.md` để xác minh các luồng end-to-end.

## Trạng thái hiện tại

Đã lên kế hoạch.