---
title: Triển khai API Truy xuất và Hủy đơn hàng
type: task
status: planned
created: 2025-07-24T03:21:16
updated: 2025-07-24T03:21:16
id: TASK-ORDER-004
priority: high
memory_types: [procedural]
dependencies: [TASK-ORDER-001, TASK-ORDER-002]
tags: [api, order-retrieval, order-cancellation, order-service]
---

## Mô tả

Triển khai các endpoint `GET /api/orders/{id}` để truy xuất thông tin chi tiết đơn hàng và `PUT /api/orders/{id}/cancel` để hủy đơn hàng. Nhiệm vụ này bao gồm việc xử lý logic truy xuất và hủy bỏ, cũng như cập nhật trạng thái đơn hàng và lịch sử trạng thái.

## Mục tiêu

*   Tạo các phương thức controller để xử lý yêu cầu `GET /api/orders/{id}` và `PUT /api/orders/{id}/cancel`.
*   Triển khai logic nghiệp vụ để truy xuất đơn hàng theo ID, bao gồm các mặt hàng trong đơn hàng.
*   Triển khai logic nghiệp vụ để hủy đơn hàng, bao gồm xác thực điều kiện hủy và cập nhật trạng thái.
*   Cập nhật lịch sử trạng thái đơn hàng khi trạng thái thay đổi.

## Danh sách kiểm tra

### Triển khai Controller
- [ ] **Thêm phương thức `getOrderById()` vào `OrderController`:**
    - **Ghi chú:** Chú thích với `@GetMapping` và `/{id}`. Trả về `OrderDetailsDTO`.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/controller/OrderController.java`.
    - **Thực hành tốt nhất:** Xử lý `OrderNotFoundException` bằng cách trả về `HttpStatus.NOT_FOUND`.
- [ ] **Thêm phương thức `cancelOrder()` vào `OrderController`:**
    - **Ghi chú:** Chú thích với `@PutMapping` và `/{id}/cancel`.
    - **Thực hành tốt nhất:** Trả về `ResponseEntity` với `HttpStatus.OK` nếu thành công.

### Logic Nghiệp vụ Truy xuất và Hủy
- [ ] **Thêm phương thức `getOrderById(Long id)` vào `OrderService`:**
    - **Ghi chú:** Truy xuất đơn hàng từ `OrderRepository`, tải các `OrderItem` liên quan.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/service/OrderService.java`.
    - **Thực hành tốt nhất:** Sử dụng `Optional` để xử lý trường hợp không tìm thấy đơn hàng.
- [ ] **Thêm phương thức `cancelOrder(Long id)` vào `OrderService`:**
    - **Ghi chú:**
        *   Truy xuất đơn hàng.
        *   Xác thực điều kiện hủy (ví dụ: đơn hàng chưa được xử lý, chưa thanh toán xong).
        *   Cập nhật trạng thái đơn hàng thành "Cancelled".
        *   Lưu lịch sử trạng thái vào `OrderStatusHistory`.
    - **Thực hành tốt nhất:** Sử dụng `@Transactional` để đảm bảo tính nhất quán. Xử lý các trường hợp không thể hủy (ví dụ: trạng thái không hợp lệ) bằng cách ném ngoại lệ nghiệp vụ.

### DTOs
- [ ] **Tạo `OrderDetailsDTO`:**
    - **Ghi chú:** Định nghĩa các trường chi tiết đơn hàng cho phản hồi API (ví dụ: `orderId`, `userId`, `totalAmount`, `status`, `createdAt`, `updatedAt`, `items: [{productId, quantity, price}]`).
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/dto/OrderDetailsDTO.java`.
- [ ] **Cập nhật `OrderCreationResponseDTO` (nếu cần):**
    - **Ghi chú:** Đảm bảo `OrderDetailsDTO` có thể tái sử dụng hoặc có mối quan hệ với nó.

## Tiến độ

*   **Triển khai Controller:** [ ]
*   **Logic Nghiệp vụ Truy xuất và Hủy:** [ ]
*   **DTOs:** [ ]

## Phụ thuộc

*   `TASK-ORDER-001-setup-project-structure.md`: Thiết lập dự án cơ bản.
*   `TASK-ORDER-002-implement-data-model-repository.md`: Các thực thể và repository đơn hàng.

## Các cân nhắc chính

*   **Xác thực trạng thái:** Đảm bảo logic hủy đơn hàng chỉ cho phép hủy ở các trạng thái hợp lệ.
*   **Thông báo cho các dịch vụ khác:** Khi đơn hàng bị hủy, có thể cần thông báo cho Inventory Service (để hoàn trả tồn kho nếu đã giữ chỗ cứng) hoặc Payment Service (để hoàn tiền nếu đã thanh toán). Điều này sẽ được xử lý trong nhiệm vụ về event publishing.

## Ghi chú

*   Bắt đầu với các chức năng truy xuất đơn giản, sau đó thêm logic hủy.
*   Sử dụng các mã trạng thái HTTP phù hợp (200 OK cho hủy thành công, 404 Not Found, 400 Bad Request cho không thể hủy).

## Thảo luận

Thảo luận về các trạng thái đơn hàng cụ thể nào cho phép hủy và các quy tắc nghiệp vụ liên quan.

## Các bước tiếp theo

Tiếp tục với `TASK-ORDER-005-implement-payment-processing-integration.md` để tích hợp xử lý thanh toán.

## Trạng thái hiện tại

Đã lên kế hoạch.