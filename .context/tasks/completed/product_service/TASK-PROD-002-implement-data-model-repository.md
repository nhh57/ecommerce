---
title: Triển khai Mô hình Dữ liệu và Repository Sản phẩm
type: task
status: completed
created: 2025-07-24T03:00:34
updated: 2025-07-24T07:23:25
id: TASK-PROD-002
priority: high
memory_types: [procedural]
dependencies: [TASK-PROD-001]
tags: [data-model, repository, persistence, product-service]
---

## Mô tả

Định nghĩa các thực thể `Product`, `ProductAttribute` và `ProductCategory` và triển khai các repository JPA tương ứng để tương tác với cơ sở dữ liệu. Điều này tạo thành lớp dữ liệu cho Dịch vụ Sản phẩm.

## Mục tiêu

*   Tạo các lớp thực thể JPA cho `Product`, `ProductAttribute` và `ProductCategory`.
*   Định nghĩa các mối quan hệ giữa các thực thể này (ví dụ: một-nhiều cho Product với ProductAttribute).
*   Triển khai các repository Spring Data JPA cho từng thực thể để cho phép các thao tác CRUD cơ bản.

## Danh sách kiểm tra

### Định nghĩa thực thể
- [x] **Tạo thực thể `Product`:**
    - **Ghi chú:** Ánh xạ tới bảng `Product` trong `Product DB`. Bao gồm các trường như `ID`, `Name`, `Description`, `Price`, `ImageURLs`, `DisplayStatus`, `CategoryID`.
    - **Vị trí:** `src/main/java/com/ecommerce/productservice/model/Product.java` (hoặc tương tự).
    - **Thực hành tốt nhất:** Sử dụng các chú thích JPA thích hợp (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`). Cân nhắc `Lob` cho `ImageURLs` nếu lưu trữ văn bản/JSON lớn, hoặc một thực thể riêng để quản lý hình ảnh.
    - **Lỗi thường gặp:** Thiếu constructor mặc định, ánh xạ cột không chính xác, các vấn đề về khóa chính.
- [x] **Tạo thực thể `ProductAttribute`:**
    - **Ghi chú:** Ánh xạ tới bảng `ProductAttribute`. Bao gồm `ID`, `ProductID`, `AttributeName`, `AttributeValue`. Định nghĩa mối quan hệ nhiều-một với `Product`.
    - **Vị trí:** `src/main/java/com/ecommerce/productservice/model/ProductAttribute.java`.
    - **Thực hành tốt nhất:** Sử dụng `@ManyToOne` với `@JoinColumn` cho các mối quan hệ.
    - **Lỗi thường gặp:** Các vấn đề về tải lười (lazy loading), sự phức tạp của mối quan hệ hai chiều.
- [x] **Tạo thực thể `ProductCategory`:**
    - **Ghi chú:** Ánh xạ tới bảng `ProductCategory`. Bao gồm `ID`, `Name`.
    - **Vị trí:** `src/main/java/com/ecommerce/productservice/model/ProductCategory.java`.
    - **Thực hành tốt nhất:** Thực thể đơn giản.
    - **Lỗi thường gặp:** Không có lỗi cụ thể.

### Triển khai Repository
- [x] **Tạo giao diện `ProductRepository`:**
    - **Ghi chú:** Kế thừa `JpaRepository<Product, Long>`.
    - **Vị trí:** `src/main/java/com/ecommerce/productservice/repository/ProductRepository.java`.
    - **Thực hành tốt nhất:** Tận dụng các phương thức truy vấn được dẫn xuất của Spring Data JPA cho các truy vấn phổ biến (ví dụ: `findByCategoryId`).
    - **Lỗi thường gặp:** Các kiểu generic không chính xác, thiếu `@Repository` (mặc dù Spring Data JPA thường suy luận được).
- [x] **Tạo giao diện `ProductAttributeRepository`:**
    - **Ghi chú:** Kế thừa `JpaRepository<ProductAttribute, Long>`.
    - **Vị trí:** `src/main/java/com/ecommerce/productservice/repository/ProductAttributeRepository.java`.
- [x] **Tạo giao diện `ProductCategoryRepository`:**
    - **Ghi chú:** Kế thừa `JpaRepository<ProductCategory, Long>`.
    - **Vị trí:** `src/main/java/com/ecommerce/productservice/repository/ProductCategoryRepository.java`.

## Tiến độ

*   **Định nghĩa thực thể:** [x]
*   **Triển khai Repository:** [x]

## Phụ thuộc

*   `TASK-PROD-001-setup-project-structure.md`: Yêu cầu thiết lập dự án cơ bản và các phụ thuộc JPA.

## Các cân nhắc chính

*   **Schema cơ sở dữ liệu:** Đảm bảo các thực thể phản ánh chính xác schema cơ sở dữ liệu đã lên kế hoạch. Sử dụng Flyway hoặc Liquibase cho các di chuyển schema trong một dự án thực tế.
*   **Hiệu suất:** Đối với `ImageURLs`, cân nhắc lưu trữ các tệp hình ảnh thực tế trong dịch vụ lưu trữ đối tượng (ví dụ: S3) và chỉ lưu trữ URL trong DB.
*   **`DisplayStatus`:** Trường này sẽ được cập nhật bởi consumer Kafka, vì vậy hãy đảm bảo nó có thể thay đổi và được xử lý chính xác.

## Ghi chú

*   Bắt đầu với ánh xạ thực thể đơn giản và thêm độ phức tạp khi cần.
*   Sử dụng các chú thích Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`) để giảm mã lặp nếu được cấu hình trong `TASK-PROD-001`.

## Thảo luận

Mô hình dữ liệu là nền tảng. Bất kỳ thay đổi nào ở đây sẽ có tác động lan tỏa. Thảo luận về cơ sở dữ liệu được chọn (PostgreSQL/MySQL) và các tính năng cụ thể của nó có thể ảnh hưởng đến thiết kế thực thể (ví dụ: hỗ trợ JSONB cho `ImageURLs` nếu lưu trữ dưới dạng mảng JSON).

## Các bước tiếp theo

Tiếp tục với `TASK-PROD-003-implement-product-read-apis.md` để hiển thị dữ liệu này thông qua API REST.

## Trạng thái hiện tại

Đã hoàn thành.