# Payment & Notification Service

## Giới thiệu
Dịch vụ xử lý thanh toán và gửi thông báo cho hệ thống đặt vé phim.

## Kiến trúc Event-Driven

### 1. Payment Service (Port 8083)
- **Lắng nghe**: `BOOKING_CREATED` event từ Booking Service
- **Xử lý**: Mô phỏng quá trình thanh toán (70% thành công)
- **Xuất bản**: `PAYMENT_COMPLETED` event (thành công hoặc thất bại)
- **Database**: `payment_notification` → bảng `payments`

**Event Flow:**
```
Booking Service
    ↓ (BOOKING_CREATED)
Payment Service
    ↓ (PAYMENT_COMPLETED)
Notification Service
```

### 2. Notification Service (cùng Port 8083)
- **Lắng nghe**: 
  - `PAYMENT_COMPLETED` event từ Payment Service
  - `BOOKING_FAILED` event từ Booking Service (nếu có)
- **Gửi**: Email và SMS notifications (mô phỏng)
- **Hỗ trợ**: Success/Failure notifications

## Bảng dữ liệu

### payments
```sql
CREATE TABLE IF NOT EXISTS payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    user_id INT NOT NULL,
    amount DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL
);
```

## Chạy dịch vụ

```bash
cd payment-notification-service
./mvnw spring-boot:run
```

## API Endpoints

### Lấy Payment bằng Booking ID
```
GET /api/payments/booking/{bookingId}
```

### Lấy Payment bằng User ID
```
GET /api/payments/user/{userId}
```

## Event Messages (RabbitMQ)

### BOOKING_CREATED Event
```json
{
  "bookingId": 1,
  "userId": 1,
  "movieId": 1,
  "seatNumbers": ["A1", "A2"],
  "totalPrice": 300000,
  "createdAt": "2026-04-21T19:50:00",
  "eventTimestamp": "2026-04-21T19:50:00"
}
```

### PAYMENT_COMPLETED Event
```json
{
  "bookingId": 1,
  "userId": 1,
  "paymentId": 1,
  "amount": 300000,
  "status": "SUCCESS",
  "paidAt": "2026-04-21T19:50:15",
  "eventTimestamp": "2026-04-21T19:50:15"
}
```

## Logs

Dịch vụ sẽ in ra console:
- 📧 Email notifications
- 📱 SMS notifications
- Payment processing status
- Event publish/consumption

## Cấu hình RabbitMQ

### Exchanges
- `booking.exchange` (Topic Exchange)

### Queues
- `payment.booking.created.queue` - Payment service lắng nghe
- `notification.payment.completed.queue` - Notification service lắng nghe
- `notification.booking.failed.queue` - Notification service lắng nghe

### Routing Keys
- `booking.created`
- `payment.completed`
- `booking.failed`
