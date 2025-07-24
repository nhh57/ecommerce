---
title: Triển khai API Đọc Sản phẩm
type: task
status: completed
created: 2025-07-24T03:00:34
updated: 2025-07-24T04:00:59
id: TASK-PROD-003
priority: high
memory_types: [procedural]
dependencies: [TASK-PROD-001, TASK-PROD-002]
tags: [api, rest, product-service]
---

## Mô tả

Triển khai các endpoint `GET /api/products` và `GET /api/products/{id}` để truy xuất thông tin sản phẩm. Nhiệm vụ này bao gồm việc tạo controller, lớp dịch vụ và tích hợp với repository.

## Mục tiêu

*   Tạo một REST controller để xử lý các yêu cầu API liên quan đến sản phẩm.
*   Triển khai một lớp dịch vụ để đóng gói logic nghiệp vụ cho việc truy xuất sản phẩm.
*   Sử dụng `ProductRepository` để lấy dữ liệu sản phẩm.
*   Định nghĩa DTOs (Data Transfer Objects) cho các phản hồi API để đảm bảo tính nhất quán dữ liệu và ẩn chi tiết thực thể nội bộ.

## Danh sách kiểm tra

### Triển khai Controller
- [x] **Tạo `ProductController`:**
    - **Ghi chú:** Chú thích với `@RestController` và `@RequestMapping("/api/products")`.
    - **Vị trí:** `src/main/java/com/ecommerce/productservice/controller/ProductController.java`.
    - **Thực hành tốt nhất:** Giữ cho controller gọn gàng, ủy quyền logic nghiệp vụ cho lớp dịch vụ.
    - **Lỗi thường gặp:** Ánh xạ yêu cầu không chính xác, thiếu `@PathVariable` hoặc `@RequestParam`.
- [x] **Triển khai endpoint `GET /api/products`:**
    - **Ghi chú:** Phương thức để truy xuất danh sách sản phẩm. Cân nhắc thêm các tham số phân trang và lọc (ví dụ: `categoryId`, `searchQuery`).
    - **Thực hành tốt nhất:** Trả về `ResponseEntity` để có phản hồi API nhất quán.
    - **Lỗi thường gặp:** Vấn đề truy vấn N+1 nếu không cẩn thận với các mối quan hệ.
- [x] **Triển khai endpoint `GET /api/products/{id}`:**
    - **Ghi chú:** Phương thức để truy xuất một sản phẩm duy nhất theo ID. Xử lý các trường hợp không tìm thấy sản phẩm (ví dụ: trả về 404 Not Found).
    - **Thực hành tốt nhất:** Sử dụng `@PathVariable` để liên kết ID từ URL.

### Triển khai Lớp Dịch vụ
- [x] **Tạo `ProductService`:**
    - **Ghi chú:** Chú thích với `@Service`. Tiêm `ProductRepository`.
    - **Vị trí:** `src/main/java/com/ecommerce/productservice/service/ProductService.java`.
    - **Thực hành tốt nhất:** Đóng gói logic nghiệp vụ tại đây.
    - **Lỗi thường gặp:** Phụ thuộc vòng tròn nếu không cẩn thận với việc tiêm dịch vụ.
- [x] **Triển khai phương thức `getAllProducts()`:**
    - **Ghi chú:** Gọi `ProductRepository` để lấy tất cả sản phẩm hoặc sản phẩm đã lọc.
    - **Thực hành tốt nhất:** Triển khai logic phân trang và lọc trong dịch vụ.
- [x] **Triển khai phương thức `getProductById(Long id)`:**
    - **Ghi chú:** Gọi `ProductRepository` để lấy một sản phẩm theo ID. Xử lý trả về `Optional` từ `findById`.

### Đối tượng Truyền dữ liệu (DTOs)
- [x] **Tạo `ProductDTO`:**
    - **Ghi chú:** Định nghĩa các trường cho phản hồi API (ví dụ: `productId`, `name`, `price`, `displayStatus`).
    - **Vị trí:** `src/main/java/com/ecommerce/productservice/dto/ProductDTO.java`.
    - **Thực hành tốt nhất:** Sử dụng một mapper (ví dụ: MapStruct, ModelMapper) để chuyển đổi các thực thể sang DTOs.
    - **Lỗi thường gặp:** Trực tiếp hiển thị các trường thực thể nhạy cảm.
- [x] **Tạo `ProductMapper` (tùy chọn nhưng được khuyến nghị):**
    - **Ghi chú:** Giao diện hoặc lớp để ánh xạ thực thể `Product` sang `ProductDTO`.

## Tiến độ

*   **Triển khai Controller:** [x]
*   **Triển khai Lớp Dịch vụ:** [x]
*   **Đối tượng Truyền dữ liệu (DTOs): [x]**

## Phụ thuộc

*   `TASK-PROD-001-setup-project-structure.md`: Thiết lập dự án và các phụ thuộc cốt lõi.
*   `TASK-PROD-002-implement-data-model-repository.md`: Các thực thể và repository sản phẩm.

## Các cân nhắc chính

*   **Thiết kế API:** Tuân thủ các nguyên tắc RESTful. Cân nhắc việc tạo phiên bản nếu API hướng ra bên ngoài.
*   **Xử lý lỗi:** Triển khai xử lý ngoại lệ toàn cục (ví dụ: `@ControllerAdvice`) để có các phản hồi lỗi nhất quán (ví dụ: 404 cho không tìm thấy, 400 cho yêu cầu không hợp lệ).
*   **Hiệu suất:** Đối với `GET /api/products`, đảm bảo việc truy xuất các danh sách lớn có hiệu quả. Tải lười (lazy loading) có thể gây ra vấn đề nếu không được quản lý.

## Ghi chú

*   Bắt đầu với các triển khai cơ bản và tinh chỉnh chúng với phân trang, lọc và xử lý lỗi.
*   Cân nhắc sử dụng `@Transactional(readOnly = true)` của Spring cho các thao tác chỉ đọc trong lớp dịch vụ để tối ưu hóa tương tác cơ sở dữ liệu.

## Thảo luận

Thiết kế của DTOs và chiến lược ánh xạ từ các thực thể nên được thảo luận. Ngoài ra, mức độ chi tiết được trả về bởi các API sản phẩm nên được làm rõ (ví dụ: bao gồm các thuộc tính theo mặc định hay là một endpoint riêng).

## Các bước tiếp theo

Tiếp tục với `TASK-PROD-004-implement-inventory-event-consumer.md` để xử lý các cập nhật tồn kho.

## Trạng thái hiện tại

Đã lên kế hoạch.