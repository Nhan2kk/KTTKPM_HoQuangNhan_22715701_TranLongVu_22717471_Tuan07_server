# Movie Ticket System — Event-Driven Architecture (EDA)

**Môn:** Kiến trúc và Thiết kế Phần mềm | **Tuần 07**

| Thành viên    | MSSV     |
| ------------- | -------- |
| Hồ Quang Nhân | 22715701 |
| Trần Long Vũ  | 22717471 |

---

## Kiến trúc

Hệ thống áp dụng **Event-Driven Architecture (EDA)** nhằm tối ưu hóa khả năng mở rộng (scalability) và xử lý bất đồng bộ. Thay vì gọi trực tiếp (synchronous), các service giao tiếp thông qua các sự kiện (events) được truyền tải qua **RabbitMQ**.

```
Frontend (React + Vite)
    │
    ▼
API Gateway :8080 
    │
    ├─► User Service    :8084
    ├─► Movie Service   :8082
    └─► Booking Service :8081 ──┐
                                │ (BookingCreated Event)
                                ▼
                             RabbitMQ :5672 (Exchange/Queue)
                                │
                                ▼
                     Payment Notification Service :8083
```

---

## Phân công

### Hồ Quang Nhân — 22715701

- **API Gateway** (`/api-gateway`)
    - Điểm tiếp nhận duy nhất cho mọi request từ Client.
    - Định tuyến động đến các Microservices và xử lý CORS.
- **Movie Service** (`/movie-service`)
    - Quản lý kho dữ liệu phim. Cung cấp API truy vấn thông tin phim.
- **User Service** (`/user-service`)
    - Quản lý tài khoản và xác thực người dùng.

### Trần Long Vũ — 22717471

- **Booking Service** (`/booking-service`) — **Event Producer**
    - Tiếp nhận yêu cầu đặt vé, lưu vào Database với trạng thái `PENDING`.
    - Phát hành (Publish) sự kiện `booking.created` vào RabbitMQ để các service khác xử lý tiếp.
- **Payment Notification Service** (`/payment-notification-service`) — **Event Consumer**
    - Lắng nghe (Subscribe) các sự kiện từ RabbitMQ.
    - Xử lý gửi thông báo xác nhận và cập nhật trạng thái giao dịch một cách bất đồng bộ.

---

## Luồng xử lý bất đồng bộ (Asynchronous)

1. **User đặt vé:** Frontend gửi `POST /api/bookings` → `Booking Service`.
2. **Phản hồi nhanh:** `Booking Service` lưu thông tin tạm thời và phản hồi ngay cho User mã `202 Accepted`.
3. **Bắn Event:** `Booking Service` đẩy sự kiện vào RabbitMQ.
4. **Tiêu thụ Event:** `Payment Notification Service` nhận sự kiện, thực hiện xử lý nghiệp vụ nặng (gửi mail, thanh toán, ...) mà không làm chậm luồng đặt vé chính.
5. **Scalability:** Hệ thống có thể xử lý hàng ngàn request đặt vé cùng lúc nhờ vào hàng đợi của RabbitMQ.

---

## Cách chạy

**Yêu cầu:** Docker Desktop, Java 17+, Node.js 18+

1. **Khởi động Hạ tầng (RabbitMQ & MariaDB):**
   ```bash
   docker compose up -d
   ```
   *Dashboard RabbitMQ: http://localhost:15672 (guest/guest)*

2. **Chạy các Microservices:**
   Mở terminal tại từng thư mục (`api-gateway`, `booking-service`, ...) và chạy:
   ```bash
   ./mvnw spring-boot:run
   ```

---

## Danh sách Service

| Service            | Port | Chức năng (EDA) | Công nghệ      |
| ------------------ | ---- | --------------- | -------------- |
| API Gateway        | 8080 | Entry Point     | Spring Cloud   |
| Booking Service    | 8081 | Event Producer  | Spring Boot    |
| Movie Service      | 8082 | Query Service   | Spring Boot    |
| Payment Notification| 8083 | Event Consumer  | Spring Boot    |
| User Service       | 8084 | Auth Service    | Spring Boot    |
| RabbitMQ           | 5672 | Message Broker  | AMQP           |
| MariaDB            | 3306 | Persistence     | SQL            |