---
title: Triển khai API Giữ chỗ Tồn kho Tạm thời (Soft Reservation)
type: task
status: active
created: 2025-07-24T03:29:29
updated: 2025-07-24T05:13:48
id: TASK-INV-003
priority: high
memory_types: [procedural]
dependencies: [TASK-INV-001, TASK-INV-002]
tags: [api, soft-reservation, inventory-service]
---

## Mô tả

Triển khai endpoint `POST /api/inventory/soft-reserve` để xử lý các yêu cầu giữ chỗ tồn kho tạm thời. Nhiệm vụ này bao gồm việc xác thực yêu cầu, kiểm tra số lượng tồn kho có sẵn, giảm số lượng tồn kho tạm thời và ghi log sự thay đổi.

## Mục tiêu

*   Tạo một REST controller để xử lý yêu cầu `POST /api/inventory/soft-reserve`.
*   Triển khai logic nghiệp vụ để thực hiện Soft Reservation.
*   Tương tác với `InventoryRepository` để cập nhật số lượng tồn kho.
*   Ghi lại sự thay đổi tồn kho vào `InventoryLog`.

## Danh sách kiểm tra

### Triển khai Controller
- [x] **Thêm phương thức `softReserve()` vào `InventoryController`:**
    - **Ghi chú:** Chú thích với `@PostMapping` và `/api/inventory/soft-reserve`. Nhận `SoftReserveRequestDTO` làm tham số.
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/controller/InventoryController.java`.
    - **Thực hành tốt nhất:** Trả về `ResponseEntity` với `HttpStatus.OK` hoặc `HttpStatus.BAD_REQUEST` tùy thuộc vào kết quả.
    - **Lỗi thường gặp:** Lỗi xác thực đầu vào, không xử lý các trường hợp lỗi nghiệp vụ.

### Logic Nghiệp vụ Soft Reservation
- [x] **Thêm phương thức `softReserve()` vào `InventoryService`:**
    - **Ghi chú:** Xử lý logic giữ chỗ tồn kho. Tiêm `InventoryRepository` và `InventoryLogRepository`.
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/service/InventoryService.java`.
    - **Thực hành tốt nhất:** Sử dụng `@Transactional` để đảm bảo tính nhất quán của giao dịch.
    - **Lỗi thường gặp:** Không xử lý các trường hợp không đủ tồn kho.
- [x] **Xác thực yêu cầu và kiểm tra tồn kho:**
    - **Ghi chú:** Kiểm tra `productId` hợp lệ và `quantity` lớn hơn 0. Truy xuất `Inventory` entity theo `productId`.
    - **Thực hành tốt nhất:** Kiểm tra `AvailableQuantity` đủ lớn cho yêu cầu.
- [x] **Giảm số lượng tồn kho tạm thời:**
    - **Ghi chú:** Giảm `AvailableQuantity` và tăng `ReservedQuantity` trong `Inventory` entity.
    - **Thực hành tốt nhất:** Sử dụng Optimistic Locking (`@Version`) để xử lý cập nhật đồng thời.
- [x] **Ghi log thay đổi tồn kho:**
    - **Ghi chú:** Tạo một bản ghi `InventoryLog` cho thao tác Soft Reservation.

### DTOs
- [x] **Tạo `SoftReserveRequestDTO`:**
    - **Ghi chú:** Định nghĩa các trường cần thiết cho yêu cầu (ví dụ: `productId`, `quantity`, `orderId`).
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/dto/SoftReserveRequestDTO.java`.
- [x] **Tạo `SoftReserveResponseDTO`:**
    - **Ghi chú:** Định nghĩa các trường cho phản hồi (ví dụ: `success`, `currentAvailableQuantity`).

## Tiến độ

*   **Triển khai Controller:** [x]
*   **Logic Nghiệp vụ Soft Reservation:** [x]
*   **DTOs:** [x]

## Phụ thuộc

*   `TASK-INV-001-setup-project-structure.md`: Thiết lập dự án cơ bản và các phụ thuộc.
*   `TASK-INV-002-implement-inventory-data-model-repository.md`: Các thực thể và repository kho hàng.

## Các cân nhắc chính

*   **Tính toàn vẹn dữ liệu:** Đảm bảo các cập nhật tồn kho là atomic và nhất quán, đặc biệt trong môi trường tải cao.
*   **Xử lý lỗi:** Làm thế nào để xử lý các trường hợp không đủ tồn kho hoặc lỗi cơ sở dữ liệu?
*   **Hiệu suất:** Tối ưu hóa truy vấn và cập nhật tồn kho để đảm bảo độ trễ thấp.

## Ghi chú

*   Bắt đầu với luồng thành công cơ bản.
*   Đảm bảo xác thực đầu vào mạnh mẽ.

## Thảo luận

Thảo luận về cách tốt nhất để xử lý các trường hợp cạnh tranh (contention) khi nhiều yêu cầu Soft Reservation cho cùng một sản phẩm đến cùng lúc.

## Các bước tiếp theo

Tiếp tục với `TASK-INV-004-implement-hard-reservation-event-consumer.md` để triển khai logic trừ tồn kho vĩnh viễn.

## Trạng thái hiện tại

Đã hoàn thành.