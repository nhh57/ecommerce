---
title: Cấu trúc dự án Dịch vụ Đặt hàng
type: task
status: planned
created: 2025-07-24T03:21:16
updated: 2025-07-24T03:21:16
id: TASK-ORDER-001
priority: high
memory_types: [procedural]
dependencies: []
tags: [setup, project-structure, order-service]
---

## Mô tả

Khởi tạo cấu trúc dự án cơ bản cho Dịch vụ Đặt hàng, bao gồm các phụ thuộc cần thiết và tệp cấu hình. Nhiệm vụ này thiết lập nền tảng cho tất cả các phát triển tiếp theo trong Dịch vụ Đặt hàng.

## Mục tiêu

*   Tạo một dự án Spring Boot mới (hoặc framework tương tự nếu không phải Java/Spring) cho Dịch vụ Đặt hàng.
*   Cấu hình các công cụ xây dựng thiết yếu (ví dụ: Maven, Gradle).
*   Thêm các phụ thuộc cốt lõi cần thiết cho một microservice (ví dụ: Spring Web, Spring Data JPA, Kafka client, RabbitMQ client).
*   Thiết lập các tệp cấu hình ứng dụng cơ bản (ví dụ: `application.properties` hoặc `application.yml`).

## Danh sách kiểm tra

### Thiết lập dự án
- [ ] **Khởi tạo dự án Spring Boot:**
    - **Ghi chú:** Sử dụng Spring Initializr hoặc IDE (IntelliJ, VS Code) để tạo một dự án Spring Boot cơ bản.
    - **Vị trí:** `src/main/java/com/ecommerce/orderservice` (hoặc cấu trúc gói tương tự).
    - **Thực hành tốt nhất:** Tuân thủ các quy ước dự án Spring Boot tiêu chuẩn.
    - **Lỗi thường gặp:** Đặt tên gói không chính xác, thiếu lớp `main` với `@SpringBootApplication`.
- [ ] **Cấu hình công cụ xây dựng (Maven/Gradle):**
    - **Ghi chú:** Thêm các plugin và kho lưu trữ cần thiết vào `pom.xml` (Maven) hoặc `build.gradle` (Gradle).
    - **Vị trí:** `pom.xml` hoặc `build.gradle` tại thư mục gốc của dự án `orderservice`.
    - **Thực hành tốt nhất:** Giữ các tệp xây dựng sạch sẽ và có tổ chức tốt.
    - **Lỗi thường gặp:** Xung đột phụ thuộc, phiên bản plugin không chính xác.

### Phụ thuộc
- [ ] **Thêm các phụ thuộc cốt lõi của Spring Boot:**
    - **Ghi chú:** Bao gồm `spring-boot-starter-web` cho API REST, `spring-boot-starter-data-jpa` cho lưu trữ, `spring-boot-starter-test` cho kiểm thử.
    - **Vị trí:** `pom.xml` hoặc `build.gradle`.
    - **Thực hành tốt nhất:** Chỉ bao gồm các starter cần thiết để giữ cho bản dựng nhẹ.
    - **Lỗi thường gặp:** Quên hậu tố `starter`, sử dụng sai ID nhóm/artifact của phụ thuộc.
- [ ] **Thêm phụ thuộc trình điều khiển cơ sở dữ liệu:**
    - **Ghi chú:** Bao gồm trình điều khiển cơ sở dữ liệu thích hợp (ví dụ: `postgresql`, `mysql`) cho `Order DB`.
    - **Vị trí:** `pom.xml` hoặc `build.gradle`.
    - **Thực hành tốt nhất:** Khớp phiên bản trình điều khiển với phiên bản cơ sở dữ liệu nếu có thể.
    - **Lỗi thường gặp:** Không khớp giữa trình điều khiển và cơ sở dữ liệu, thiếu trình điều khiển.
- [ ] **Thêm phụ thuộc Kafka Client:**
    - **Ghi chú:** Bao gồm `spring-kafka` hoặc `kafka-clients` để tích hợp Kafka.
    - **Vị trí:** `pom.xml` hoặc `build.gradle`.
    - **Thực hành tốt nhất:** Sử dụng `spring-kafka` để tích hợp Spring dễ dàng hơn.
    - **Lỗi thường gặp:** Không tương thích phiên bản với Kafka broker.
- [ ] **Thêm phụ thuộc RabbitMQ Client:**
    - **Ghi chú:** Bao gồm `spring-rabbit` hoặc `amqp-client` để tích hợp RabbitMQ.
    - **Vị trí:** `pom.xml` hoặc `build.gradle`.
    - **Thực hành tốt nhất:** Sử dụng `spring-rabbit` để tích hợp Spring dễ dàng hơn.
    - **Lỗi thường gặp:** Không tương thích phiên bản với RabbitMQ broker.

### Cấu hình
- [ ] **Thiết lập `application.properties`/`application.yml`:**
    - **Ghi chú:** Cấu hình cổng server cơ bản, thuộc tính kết nối cơ sở dữ liệu (URL, tên người dùng, mật khẩu), và địa chỉ Kafka/RabbitMQ broker.
    - **Vị trí:** `src/main/resources/application.properties` hoặc `application.yml`.
    - **Thực hành tốt nhất:** Sử dụng biến môi trường cho thông tin nhạy cảm trong môi trường sản xuất.
    - **Lỗi thường gặp:** Lỗi chính tả trong tên thuộc tính, chuỗi kết nối không chính xác.

## Tiến độ

*   **Thiết lập dự án:** [ ]
*   **Phụ thuộc:** [ ]
*   **Cấu hình:** [ ]

## Phụ thuộc

Không có, đây là nhiệm vụ thiết lập ban đầu.

## Các cân nhắc chính

*   **Lựa chọn công cụ xây dựng:** Maven hoặc Gradle.
*   **Cơ sở dữ liệu:** Đảm bảo cơ sở dữ liệu được chọn tương thích với cấu trúc thực thể `Order` và các yêu cầu về hiệu suất.
*   **Microservice Principles:** Giữ cho dịch vụ tập trung vào các mối quan tâm liên quan đến đơn hàng, tránh mở rộng phạm vi không cần thiết.

## Ghi chú

*   Bắt đầu với cấu trúc dự án tối thiểu khả thi để đảm bảo vòng phản hồi nhanh chóng.
*   Sử dụng quy ước đặt tên nhất quán cho các gói và lớp.
*   Cân nhắc sử dụng Lombok để giảm mã lặp (ví dụ: getters/setters) nếu nhóm đồng ý.

## Thảo luận

Nhiệm vụ này đặt nền móng. Các nhiệm vụ trong tương lai sẽ xây dựng dựa trên nền tảng này. Lựa chọn cơ sở dữ liệu và thư viện Message Queue cụ thể nên được thảo luận nếu có các ưu tiên hoặc ràng buộc cơ sở hạ tầng hiện có.

## Các bước tiếp theo

Tiếp tục với `TASK-ORDER-002-implement-data-model-repository.md` để định nghĩa mô hình dữ liệu và giao diện repository.

## Trạng thái hiện tại

Đã lên kế hoạch.