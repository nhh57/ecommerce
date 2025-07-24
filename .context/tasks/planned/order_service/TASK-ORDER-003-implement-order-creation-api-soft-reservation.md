---
title: Triển khai API Tạo đơn hàng và Logic Giữ chỗ Tồn kho Tạm thời
type: task
status: planned
created: 2025-07-24T03:21:16
updated: 2025-07-24T03:21:16
id: TASK-ORDER-003
priority: high
memory_types: [procedural]
dependencies: [TASK-ORDER-001, TASK-ORDER-002]
tags: [api, order-creation, soft-reservation, order-service]
---

## Mô tả

Triển khai endpoint `POST /api/orders` để tạo đơn hàng mới. Nhiệm vụ này bao gồm việc xử lý yêu cầu tạo đơn hàng, tương tác với Dịch vụ Kho hàng để thực hiện giữ chỗ tồn kho tạm thời (Soft Reservation), và lưu trữ đơn hàng với trạng thái ban đầu.

## Mục tiêu

*   Tạo một REST controller để xử lý yêu cầu `POST /api/orders`.
*   Triển khai logic nghiệp vụ để tạo đơn hàng mới.
*   Tương tác với Dịch vụ Kho hàng để thực hiện Soft Reservation.
*   Lưu đơn hàng vào cơ sở dữ liệu với trạng thái "Pending Payment" (hoặc trạng thái ban đầu khác).
*   Định nghĩa DTOs cho yêu cầu và phản hồi API.

## Danh sách kiểm tra

### Triển khai Controller
- [ ] **Thêm phương thức `createOrder()` vào `OrderController`:**
    - **Ghi chú:** Chú thích với `@PostMapping` và `/api/orders`. Nhận `OrderCreationRequestDTO` làm tham số.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/controller/OrderController.java`.
    - **Thực hành tốt nhất:** Trả về `ResponseEntity` với `HttpStatus.CREATED` nếu thành công.
    - **Lỗi thường gặp:** Thiếu `@RequestBody`, lỗi xác thực đầu vào.

### Logic Nghiệp vụ Tạo đơn hàng
- [ ] **Thêm phương thức `createOrder()` vào `OrderService`:**
    - **Ghi chú:** Xử lý logic tạo đơn hàng. Tiêm `OrderRepository` và `InventoryServiceClient` (hoặc tương tác với Inventory Service qua một lớp client/feign client).
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/service/OrderService.java`.
    - **Thực hành tốt nhất:** Sử dụng `@Transactional` để đảm bảo tính nhất quán của giao dịch.
    - **Lỗi thường gặp:** Không xử lý các trường hợp ngoại lệ từ Inventory Service.
- [ ] **Thực hiện cuộc gọi Soft Reservation đến Inventory Service:**
    - **Ghi chú:** Trước khi lưu đơn hàng, gọi API giữ chỗ tồn kho tạm thời của Inventory Service.
    - **Thực hành tốt nhất:** Xử lý kết quả trả về từ Inventory Service (thành công/thất bại).
    - **Lỗi thường gặp:** Không xử lý trường hợp giữ chỗ tồn kho thất bại (ví dụ: hết hàng).
- [ ] **Lưu đơn hàng vào `Order DB`:**
    - **Ghi chú:** Nếu Soft Reservation thành công, lưu đơn hàng với trạng thái "Pending Payment".
    - **Thực hành tốt nhất:** Ghi lại `OrderStatusHistory` cho trạng thái ban đầu.

### DTOs
- [ ] **Tạo `OrderCreationRequestDTO`:**
    - **Ghi chú:** Định nghĩa các trường cần thiết để tạo đơn hàng (ví dụ: `userId`, `items: [{productId, quantity}]`).
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/dto/OrderCreationRequestDTO.java`.
    - **Thực hành tốt nhất:** Sử dụng các chú thích xác thực (ví dụ: `@NotNull`, `@Min`).
- [ ] **Tạo `OrderCreationResponseDTO`:**
    - **Ghi chú:** Định nghĩa các trường cho phản hồi sau khi tạo đơn hàng (ví dụ: `orderId`, `status`).

## Tiến độ

*   **Triển khai Controller:** [ ]
*   **Logic Nghiệp vụ Tạo đơn hàng:** [ ]
*   **DTOs:** [ ]

## Phụ thuộc

*   `TASK-ORDER-001-setup-project-structure.md`: Thiết lập dự án cơ bản và các phụ thuộc.
*   `TASK-ORDER-002-implement-data-model-repository.md`: Các thực thể và repository đơn hàng.

## Các cân nhắc chính

*   **Xử lý lỗi:** Làm thế nào để xử lý các lỗi khi gọi Inventory Service (ví dụ: hết hàng, dịch vụ không khả dụng)? Nên hoàn tác (rollback) đơn hàng đã được tạo tạm thời không?
*   **Tính bất biến (Idempotency):** Cân nhắc cơ chế để đảm bảo các yêu cầu tạo đơn hàng trùng lặp từ người dùng không dẫn đến việc tạo nhiều đơn hàng.
*   **Thời gian phản hồi:** Giữ cho thời gian phản hồi của API tạo đơn hàng nhanh chóng, các tác vụ nặng hơn sẽ được xử lý bất đồng bộ.

## Ghi chú

*   Bắt đầu với luồng cơ bản: tạo đơn hàng -> gọi Inventory -> lưu đơn hàng. Xử lý thanh toán sẽ là nhiệm vụ tiếp theo.
*   Đảm bảo xác thực đầu vào mạnh mẽ cho `OrderCreationRequestDTO`.

## Thảo luận

Thảo luận về cơ chế giao tiếp với Inventory Service (ví dụ: REST sync call, event-driven, Feign Client). Quyết định về cách xử lý lỗi khi Inventory Service phản hồi hết hàng.

## Các bước tiếp theo

Tiếp tục với `TASK-ORDER-004-implement-order-retrieval-cancellation-apis.md` để triển khai các API truy xuất và hủy đơn hàng.

## Trạng thái hiện tại

Đã lên kế hoạch.