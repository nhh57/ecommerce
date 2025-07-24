---
title: Triển khai Mô hình Dữ liệu và Repository Kho hàng
type: task
status: planned
created: 2025-07-24T03:29:29
updated: 2025-07-24T03:29:29
id: TASK-INV-002
priority: high
memory_types: [procedural]
dependencies: [TASK-INV-001]
tags: [data-model, repository, persistence, inventory-service]
---

## Mô tả

Định nghĩa các thực thể `Inventory` và `InventoryLog` và triển khai các repository JPA tương ứng để tương tác với cơ sở dữ liệu. Điều này tạo thành lớp dữ liệu cho Dịch vụ Kho hàng.

## Mục tiêu

*   Tạo các lớp thực thể JPA cho `Inventory` và `InventoryLog`.
*   Định nghĩa mối quan hệ giữa các thực thể này (ví dụ: một-nhiều cho Inventory với InventoryLog).
*   Triển khai các repository Spring Data JPA cho từng thực thể để cho phép các thao tác CRUD cơ bản.

## Danh sách kiểm tra

### Định nghĩa thực thể
- [ ] **Tạo thực thể `Inventory`:**
    - **Ghi chú:** Ánh xạ tới bảng `Inventory` trong `Inventory DB`. Bao gồm các trường như `ProductID`, `AvailableQuantity`, `ReservedQuantity`, `Version`, `LastUpdatedAt`.
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/model/Inventory.java` (hoặc tương tự).
    - **Thực hành tốt nhất:** Sử dụng các chú thích JPA thích hợp (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`, `@Version` cho optimistic locking).
    - **Lỗi thường gặp:** Thiếu constructor mặc định, ánh xạ cột không chính xác, vấn đề về khóa chính.
- [ ] **Tạo thực thể `InventoryLog`:**
    - **Ghi chú:** Ánh xạ tới bảng `InventoryLog`. Bao gồm `LogID`, `ProductID`, `ChangeType`, `QuantityChange`, `NewQuantity`, `Timestamp`, `OrderID`.
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/model/InventoryLog.java`.
    - **Thực hành tốt nhất:** Đơn giản và rõ ràng để theo dõi lịch sử thay đổi tồn kho.

### Triển khai Repository
- [ ] **Tạo giao diện `InventoryRepository`:**
    - **Ghi chú:** Kế thừa `JpaRepository<Inventory, Long>`.
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/repository/InventoryRepository.java`.
    - **Thực hành tốt nhất:** Tận dụng các phương thức truy vấn được dẫn xuất của Spring Data JPA (ví dụ: `findByProductId`).
    - **Lỗi thường gặp:** Các kiểu generic không chính xác, thiếu `@Repository`.
- [ ] **Tạo giao diện `InventoryLogRepository`:**
    - **Ghi chú:** Kế thừa `JpaRepository<InventoryLog, Long>`.
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/repository/InventoryLogRepository.java`.

## Tiến độ

*   **Định nghĩa thực thể:** [ ]
*   **Triển khai Repository:** [ ]

## Phụ thuộc

*   `TASK-INV-001-setup-project-structure.md`: Yêu cầu thiết lập dự án cơ bản và các phụ thuộc JPA.

## Các cân nhắc chính

*   **Schema cơ sở dữ liệu:** Đảm bảo các thực thể phản ánh chính xác schema cơ sở dữ liệu đã lên kế hoạch. Sử dụng Flyway hoặc Liquibase cho các di chuyển schema trong một dự án thực tế.
*   **Optimistic Locking:** Việc sử dụng trường `Version` trong `Inventory` entity là rất quan trọng để xử lý các cập nhật đồng thời.

## Ghi chú

*   Bắt đầu với ánh xạ thực thể đơn giản và thêm độ phức tạp khi cần.
*   Cân nhắc sử dụng Lombok annotations (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`) để giảm mã lặp.

## Thảo luận

Mô hình dữ liệu là nền tảng. Bất kỳ thay đổi nào ở đây sẽ có tác động lan tỏa. Thảo luận về cơ sở dữ liệu được chọn (PostgreSQL/MySQL) và các tính năng cụ thể của nó có thể ảnh hưởng đến thiết kế thực thể.

## Các bước tiếp theo

Tiếp tục với `TASK-INV-003-implement-soft-reservation-api.md` để triển khai API giữ chỗ tồn kho tạm thời.

## Trạng thái hiện tại

Đã lên kế hoạch.