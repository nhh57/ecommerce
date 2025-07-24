---
title: Triển khai Caching Kho hàng
type: task
status: planned
created: 2025-07-24T03:29:29
updated: 2025-07-24T03:29:29
id: TASK-INV-007
priority: high
memory_types: [procedural]
dependencies: [TASK-INV-001, TASK-INV-002]
tags: [caching, redis, performance, inventory-service]
---

## Mô tả

Tích hợp Redis để caching tồn kho của các sản phẩm "hot" nhằm đạt thông lượng cao và độ trễ thấp. Nhiệm vụ này bao gồm việc cấu hình Redis, sử dụng các thao tác atomic trên Redis, và cân nhắc cơ chế đồng bộ hóa giữa Redis và Inventory DB.

## Mục tiêu

*   Cấu hình Redis client trong Dịch vụ Kho hàng.
*   Sử dụng Redis làm lớp cache hoặc nguồn tồn kho chính cho các sản phẩm hot.
*   Triển khai các thao tác atomic (ví dụ: `DECRBY`, `INCRBY`) trên Redis.
*   Cân nhắc cơ chế đồng bộ dữ liệu giữa Redis và Inventory DB bền vững.

## Danh sách kiểm tra

### Cấu hình Redis
- [ ] **Thêm phụ thuộc Redis:**
    - **Ghi chú:** Bao gồm `spring-boot-starter-data-redis`.
    - **Vị trí:** `pom.xml` hoặc `build.gradle`.
    - **Thực hành tốt nhất:** Đảm bảo cấu hình Redis phù hợp với môi trường.
    - **Lỗi thường gặp:** Thiếu phụ thuộc, cấu hình kết nối Redis không chính xác.
- [ ] **Cấu hình RedisTemplate:**
    - **Ghi chú:** Cấu hình `RedisTemplate` để tương tác với Redis.
    - **Vị trí:** Lớp cấu hình Redis chuyên dụng.
    - **Thực hành tốt nhất:** Sử dụng serializer phù hợp (ví dụ: JSON, String).

### Triển khai Caching
- [ ] **Cập nhật logic `softReserve()` và `hardReserve()` để tương tác với Redis:**
    - **Ghi chú:** Trước khi cập nhật DB, kiểm tra và cập nhật tồn kho trong Redis bằng các thao tác atomic (ví dụ: `opsForValue().decrement()`).
    - **Vị trí:** `src/main/java/com/ecommerce/inventoryservice/service/InventoryService.java`.
    - **Thực hành tốt nhất:** Đảm bảo tính nhất quán giữa Redis và DB. Xử lý các trường hợp thất bại của Redis.
- [ ] **Cập nhật logic `rollbackSoftReservation()` để tương tác với Redis:**
    - **Ghi chú:** Tăng số lượng tồn kho trong Redis bằng các thao tác atomic.
- [ ] **Triển khai `GET /api/inventory/{productId}` để đọc từ Redis:**
    - **Ghi chú:** Ưu tiên đọc thông tin tồn kho từ Redis trước khi truy vấn DB.

### Đồng bộ hóa Redis và DB
- [ ] **Cân nhắc cơ chế đồng bộ hóa:**
    - **Ghi chú:** Đề xuất và triển khai một cơ chế để đảm bảo tính nhất quán giữa Redis và Inventory DB (ví dụ: write-through, write-back, hoặc đồng bộ bất đồng bộ thông qua Message Queue).
    - **Thực hành tốt nhất:** Đảm bảo dữ liệu trong Redis có thể được phục hồi từ DB nếu Redis bị mất dữ liệu.

## Tiến độ

*   **Cấu hình Redis:** [ ]
*   **Triển khai Caching:** [ ]
*   **Đồng bộ hóa Redis và DB:** [ ]

## Phụ thuộc

*   `TASK-INV-001-setup-project-structure.md`: Thiết lập dự án và phụ thuộc Redis client.
*   `TASK-INV-002-implement-inventory-data-model-repository.md`: Các thực thể kho hàng.

## Các cân nhắc chính

*   **Tính nhất quán dữ liệu:** Đảm bảo dữ liệu tồn kho trong Redis và DB luôn nhất quán. Đây là một thách thức lớn trong môi trường tải cao.
*   **Hiệu suất:** Tối ưu hóa các thao tác đọc/ghi trên Redis để đạt được độ trễ thấp nhất.
*   **Khả năng phục hồi:** Đảm bảo dữ liệu tồn kho trong Redis có thể được phục hồi hoặc tái tạo từ nguồn bền vững (DB) nếu có sự cố.

## Ghi chú

*   Bắt đầu với việc sử dụng Redis cho các thao tác đọc và viết đơn giản, sau đó mở rộng để đồng bộ hóa phức tạp hơn.
*   Theo dõi các chỉ số Redis (hit ratio, memory usage) để đánh giá hiệu suất.

## Thảo luận

Thảo luận sâu về chiến lược đồng bộ hóa giữa Redis và DB, cũng như cách xử lý các trường hợp cạnh tranh (race conditions) khi cập nhật tồn kho.

## Các bước tiếp theo

Tiếp tục với `TASK-INV-008-implement-concurrency-control.md` để triển khai kiểm soát đồng thời.

## Trạng thái hiện tại

Đã lên kế hoạch.