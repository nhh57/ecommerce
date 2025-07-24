---
title: Triển khai Kiểm soát Đồng thời
type: task
status: planned
created: 2025-07-24T03:29:29
updated: 2025-07-24T03:29:29
id: TASK-INV-008
priority: high
memory_types: [procedural]
dependencies: [TASK-INV-002, TASK-INV-003, TASK-INV-004, TASK-INV-005, TASK-INV-007]
tags: [concurrency, optimistic-locking, cas, inventory-service]
---

## Mô tả

Triển khai và đảm bảo các cơ chế kiểm soát đồng thời hiệu quả cho các thao tác cập nhật tồn kho. Nhiệm vụ này tập trung vào việc sử dụng Optimistic Locking cho cơ sở dữ liệu và Compare-and-Swap (CAS) cho các thao tác trên Redis.

## Mục tiêu

*   Đảm bảo Optimistic Locking được triển khai và hoạt động chính xác cho các cập nhật tồn kho trong `Inventory DB`.
*   Đảm bảo các thao tác trên Redis sử dụng CAS hoặc các lệnh atomic để xử lý đồng thời.
*   Xử lý các xung đột đồng thời một cách duyên dáng (ví dụ: retry).

## Danh sách kiểm tra

### Optimistic Locking (DB)
- [ ] **Xác minh và sử dụng `@Version` trong `Inventory` entity:**
    - **Ghi chú:** Đảm bảo trường `version` đã được thêm vào `Inventory` entity và được chú thích với `@Version`.
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/model/Inventory.java`.
    - **Thực hành tốt nhất:** Cho phép JPA tự động quản lý trường `version`.
- [ ] **Xử lý `OptimisticLockingFailureException`:**
    - **Ghi chú:** Triển khai logic để bắt `OptimisticLockingFailureException` (hoặc `ObjectOptimisticLockingFailureException`) khi cập nhật tồn kho và thực hiện retry.
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/service/InventoryService.java` (trong các phương thức `softReserve`, `hardReserveInventory`, `rollbackSoftReservation`).
    - **Thực hành tốt nhất:** Sử dụng một cơ chế retry với exponential backoff để tránh làm quá tải DB.

### Compare-and-Swap (CAS) (Redis)
- [ ] **Sử dụng các thao tác atomic trên Redis:**
    - **Ghi chú:** Đối với các cập nhật tồn kho trực tiếp trên Redis, sử dụng các lệnh atomic như `DECRBY`, `INCRBY`.
    - **Thực hành tốt nhất:** Nếu cần logic phức tạp hơn, sử dụng `WATCH`/`MULTI`/`EXEC` để đảm bảo tính nguyên tử của một khối lệnh.
    - **Lỗi thường gặp:** Không sử dụng các lệnh atomic, dẫn đến các điều kiện chạy.
- [ ] **Xử lý các xung đột CAS:**
    - **Ghi chú:** Triển khai logic để bắt các lỗi CAS và thực hiện retry.

## Tiến độ

*   **Optimistic Locking (DB):** [ ]
*   **Compare-and-Swap (CAS) (Redis):** [ ]

## Phụ thuộc

*   `TASK-INV-002-implement-inventory-data-model-repository.md`: `Inventory` entity với trường `version`.
*   `TASK-INV-003-implement-soft-reservation-api.md`: Logic Soft Reservation.
*   `TASK-INV-004-implement-hard-reservation-event-consumer.md`: Logic Hard Reservation.
*   `TASK-INV-005-implement-rollback-soft-reservation-event-consumer.md`: Logic Rollback Soft Reservation.
*   `TASK-INV-007-implement-inventory-caching.md`: Logic caching Redis.

## Các cân nhắc chính

*   **Hiệu suất:** Các cơ chế kiểm soát đồng thời có thể ảnh hưởng đến hiệu suất. Chọn giải pháp phù hợp với yêu cầu thông lượng.
*   **Tính đúng đắn:** Đảm bảo rằng không có trường hợp cạnh tranh nào dẫn đến dữ liệu tồn kho không chính xác hoặc bán quá số lượng.
*   **Chiến lược retry:** Số lần retry và khoảng thời gian chờ giữa các lần retry cần được điều chỉnh cẩn thận.

## Ghi chú

*   Kiểm tra kỹ lưỡng các kịch bản đồng thời.
*   Sử dụng các công cụ kiểm thử hiệu suất để mô phỏng tải cao và kiểm tra hành vi đồng thời.

## Thảo luận

Thảo luận về sự cân bằng giữa tính đúng đắn và hiệu suất khi xử lý đồng thời, đặc biệt trong các sự kiện sale.

## Các bước tiếp theo

Tiếp tục với `TASK-INV-009-implement-unit-tests.md` để đảm bảo chất lượng mã.

## Trạng thái hiện tại

Đã lên kế hoạch.