---
title: Triển khai Mô hình Dữ liệu và Repository Đặt hàng
type: task
status: planned
created: 2025-07-24T03:21:16
updated: 2025-07-24T03:21:16
id: TASK-ORDER-002
priority: high
memory_types: [procedural]
dependencies: [TASK-ORDER-001]
tags: [data-model, repository, persistence, order-service]
---

## Mô tả

Định nghĩa các thực thể `Order`, `OrderItem` và `OrderStatusHistory` và triển khai các repository JPA tương ứng để tương tác với cơ sở dữ liệu. Điều này tạo thành lớp dữ liệu cho Dịch vụ Đặt hàng.

## Mục tiêu

*   Tạo các lớp thực thể JPA cho `Order`, `OrderItem` và `OrderStatusHistory`.
*   Định nghĩa các mối quan hệ giữa các thực thể này (ví dụ: một-nhiều cho Order với OrderItem, một-nhiều cho Order với OrderStatusHistory).
*   Triển khai các repository Spring Data JPA cho từng thực thể để cho phép các thao tác CRUD cơ bản.

## Danh sách kiểm tra

### Định nghĩa thực thể
- [ ] **Tạo thực thể `Order`:**
    - **Ghi chú:** Ánh xạ tới bảng `Order` trong `Order DB`. Bao gồm các trường như `ID`, `UserID`, `TotalAmount`, `Status`, `CreatedAt`, `UpdatedAt`, `PaymentID`.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/model/Order.java` (hoặc tương tự).
    - **Thực hành tốt nhất:** Sử dụng các chú thích JPA thích hợp (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`).
    - **Lỗi thường gặp:** Thiếu constructor mặc định, ánh xạ cột không chính xác, vấn đề về khóa chính.
- [ ] **Tạo thực thể `OrderItem`:**
    - **Ghi chú:** Ánh xạ tới bảng `OrderItem`. Bao gồm `ID`, `OrderID`, `ProductID`, `Quantity`, `Price`. Định nghĩa mối quan hệ nhiều-một với `Order`.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/model/OrderItem.java`.
    - **Thực hành tốt nhất:** Sử dụng `@ManyToOne` với `@JoinColumn` cho các mối quan hệ.
    - **Lỗi thường gặp:** Các vấn đề về tải lười (lazy loading), sự phức tạp của mối quan hệ hai chiều.
- [ ] **Tạo thực thể `OrderStatusHistory`:**
    - **Ghi chú:** Ánh xạ tới bảng `OrderStatusHistory`. Bao gồm `ID`, `OrderID`, `OldStatus`, `NewStatus`, `ChangedAt`. Định nghĩa mối quan hệ nhiều-một với `Order`.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/model/OrderStatusHistory.java`.
    - **Thực hành tốt nhất:** Đơn giản và rõ ràng để theo dõi lịch sử trạng thái.
    - **Lỗi thường gặp:** Không có lỗi cụ thể.

### Triển khai Repository
- [ ] **Tạo giao diện `OrderRepository`:**
    - **Ghi chú:** Kế thừa `JpaRepository<Order, Long>`.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/repository/OrderRepository.java`.
    - **Thực hành tốt nhất:** Tận dụng các phương thức truy vấn được dẫn xuất của Spring Data JPA cho các truy vấn phổ biến.
    - **Lỗi thường gặp:** Các kiểu generic không chính xác, thiếu `@Repository`.
- [ ] **Tạo giao diện `OrderItemRepository`:**
    - **Ghi chú:** Kế thừa `JpaRepository<OrderItem, Long>`.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/repository/OrderItemRepository.java`.
- [ ] **Tạo giao diện `OrderStatusHistoryRepository`:**
    - **Ghi chú:** Kế thừa `JpaRepository<OrderStatusHistory, Long>`.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice/repository/OrderStatusHistoryRepository.java`.

## Tiến độ

*   **Định nghĩa thực thể:** [ ]
*   **Triển khai Repository:** [ ]

## Phụ thuộc

*   `TASK-ORDER-001-setup-project-structure.md`: Yêu cầu thiết lập dự án cơ bản và các phụ thuộc JPA.

## Các cân nhắc chính

*   **Schema cơ sở dữ liệu:** Đảm bảo các thực thể phản ánh chính xác schema cơ sở dữ liệu đã lên kế hoạch. Sử dụng Flyway hoặc Liquibase cho các di chuyển schema trong một dự án thực tế.
*   **Trạng thái đơn hàng:** Sử dụng một enum hoặc một tập hợp các hằng số để định nghĩa các trạng thái đơn hàng.

## Ghi chú

*   Bắt đầu với ánh xạ thực thể đơn giản và thêm độ phức tạp khi cần.
*   Cân nhắc sử dụng Lombok annotations (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`) để giảm mã lặp.

## Thảo luận

Mô hình dữ liệu là nền tảng. Bất kỳ thay đổi nào ở đây sẽ có tác động lan tỏa. Thảo luận về cơ sở dữ liệu được chọn (PostgreSQL/MySQL) và các tính năng cụ thể của nó có thể ảnh hưởng đến thiết kế thực thể.

## Các bước tiếp theo

Tiếp tục với `TASK-ORDER-003-implement-order-creation-api-soft-reservation.md` để triển khai API tạo đơn hàng.

## Trạng thái hiện tại

Đã lên kế hoạch.