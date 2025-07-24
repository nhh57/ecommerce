---
title: Triển khai Unit Test cho Dịch vụ Kho hàng
type: task
status: planned
created: 2025-07-24T03:29:29
updated: 2025-07-24T03:29:29
id: TASK-INV-009
priority: high
memory_types: [procedural]
dependencies: [TASK-INV-002, TASK-INV-003, TASK-INV-004, TASK-INV-005]
tags: [testing, unit-test, inventory-service]
---

## Mô tả

Viết các bài kiểm tra đơn vị toàn diện cho logic cốt lõi, các phương thức repository, logic xử lý API và event trong Dịch vụ Kho hàng. Điều này đảm bảo rằng các thành phần riêng lẻ của dịch vụ hoạt động chính xác một cách độc lập.

## Mục tiêu

*   Đạt được độ bao phủ mã cao cho logic nghiệp vụ của `InventoryService`.
*   Xác minh hành vi chính xác của các phương thức `InventoryRepository`.
*   Đảm bảo logic Soft Reservation, Hard Reservation, Rollback và xuất bản/tiêu thụ sự kiện hoạt động như mong đợi.

## Danh sách kiểm tra

### Unit Test cho `InventoryService`
- [ ] **Kiểm tra `softReserve()`:**
    - **Ghi chú:** Xác minh rằng phương thức xử lý thành công việc giữ chỗ tồn kho, cập nhật `AvailableQuantity` và `ReservedQuantity` chính xác. Kiểm tra các trường hợp thất bại (ví dụ: không đủ tồn kho).
    - **Vị trí:** `src/test/java/com/ecommerce/inventoryservice/service/InventoryServiceTest.java`.
    - **Thực hành tốt nhất:** Sử dụng Mockito để giả lập `InventoryRepository` và Kafka Producer.
- [ ] **Kiểm tra `hardReserveInventory()`:**
    - **Ghi chú:** Xác minh rằng phương thức xử lý thành công việc trừ tồn kho vĩnh viễn và cập nhật `AvailableQuantity` và `ReservedQuantity` chính xác.
- [ ] **Kiểm tra `rollbackSoftReservation()`:**
    - **Ghi chú:** Xác minh rằng phương thức xử lý thành công việc hoàn trả tồn kho tạm thời và cập nhật `AvailableQuantity` và `ReservedQuantity` chính xác.

### Unit Test cho Repositories
- [ ] **Kiểm tra `InventoryRepository` và `InventoryLogRepository`:**
    - **Ghi chú:** Sử dụng `@DataJpaTest` để kiểm tra các phương thức repository. Test các thao tác CRUD cơ bản và các truy vấn tùy chỉnh (ví dụ: tìm kiếm theo `productId`).
    - **Vị trí:** `src/test/java/com/ecommerce/inventoryservice/repository/InventoryRepositoryTest.java`.
    - **Thực hành tốt nhất:** Sử dụng cơ sở dữ liệu trong bộ nhớ (ví dụ: H2) cho các bài kiểm tra nhanh.

### Unit Test cho Controllers
- [ ] **Kiểm tra `InventoryController`:**
    - **Ghi chú:** Sử dụng `@WebMvcTest` hoặc `@SpringBootTest` với MockMvc để kiểm tra các endpoint API (ví dụ: `soft-reserve`).
    - **Vị trí:** `src/test/java/com/ecommerce/inventoryservice/controller/InventoryControllerTest.java`.
    - **Thực hành tốt nhất:** Tập trung vào việc kiểm tra ánh xạ yêu cầu, xác thực đầu vào và phản hồi API.

### Unit Test cho Event Handling
- [ ] **Kiểm tra Kafka Consumer (`OrderCreatedListener`, `OrderPaymentFailedListener`):**
    - **Ghi chú:** Kiểm tra rằng listener xử lý đúng các sự kiện đến và gọi đúng logic nghiệp vụ trong `InventoryService`.
    - **Vị trí:** `src/test/java/com/ecommerce/inventoryservice/listener/OrderCreatedListenerTest.java`, `OrderPaymentFailedListenerTest.java`.
    - **Thực hành tốt nhất:** Sử dụng Mockito để giả lập `InventoryService`.
- [ ] **Kiểm tra Kafka Producer (cho `inventory.updated`):**
    - **Ghi chú:** Xác minh rằng sự kiện `inventory.updated` được gửi đi với nội dung chính xác sau các thay đổi tồn kho.

## Tiến độ

*   **Unit Test cho `InventoryService`:** [ ]
*   **Unit Test cho Repositories:** [ ]
*   **Unit Test cho Controllers:** [ ]
*   **Unit Test cho Event Handling:** [ ]

## Phụ thuộc

*   `TASK-INV-002-implement-inventory-data-model-repository.md`: Các thực thể và repository phải được triển khai.
*   `TASK-INV-003-implement-soft-reservation-api.md`: Logic Soft Reservation.
*   `TASK-INV-004-implement-hard-reservation-event-consumer.md`: Logic Hard Reservation.
*   `TASK-INV-005-implement-rollback-soft-reservation-event-consumer.md`: Logic Rollback Soft Reservation.

## Các cân nhắc chính

*   **Test Coverage:** Ưu tiên kiểm tra các đường dẫn quan trọng và logic phức tạp, đặc biệt là các phần liên quan đến cập nhật tồn kho và xử lý đồng thời.
*   **Test Data:** Sử dụng dữ liệu kiểm tra thực tế nhưng cô lập.
*   **Mocking:** Giả lập các phụ thuộc bên ngoài (Kafka).

## Ghi chú

*   Tuân theo mẫu Arrange-Act-Assert (AAA) cho cấu trúc kiểm tra.
*   Cung cấp tên phương thức kiểm tra rõ ràng và mô tả.

## Thảo luận

Thảo luận về các trường hợp biên và kịch bản lỗi cần được kiểm tra kỹ lưỡng, đặc biệt là các tình huống đồng thời.

## Các bước tiếp theo

Tiếp tục với `TASK-INV-010-implement-integration-tests.md` để xác minh các luồng end-to-end.

## Trạng thái hiện tại

Đã lên kế hoạch.