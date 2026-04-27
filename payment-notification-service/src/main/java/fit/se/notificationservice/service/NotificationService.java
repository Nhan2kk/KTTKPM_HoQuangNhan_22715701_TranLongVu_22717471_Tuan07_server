package fit.se.notificationservice.service;

import fit.se.notificationservice.config.RabbitMQConfig;
import fit.se.paymentservice.event.PaymentCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class NotificationService {

    /**
     * Listen to PAYMENT_COMPLETED event from Payment Service
     * Send email/SMS notification to user
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_COMPLETED_QUEUE)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        try {
            log.info("Received PAYMENT_COMPLETED event - Booking ID: {}, Status: {}", 
                    event.getBookingId(), event.getStatus());

            if ("SUCCESS".equals(event.getStatus())) {
                sendSuccessNotification(event);
            } else {
                sendFailureNotification(event);
            }

        } catch (Exception e) {
            log.error("Error handling payment completed notification", e);
        }
    }

    /**
     * Listen to BOOKING_FAILED event
     * Send failure notification to user
     */
    @RabbitListener(queues = RabbitMQConfig.BOOKING_FAILED_QUEUE)
    public void handleBookingFailed(PaymentCompletedEvent event) {
        try {
            log.warn("Received BOOKING_FAILED event - Booking ID: {}", event.getBookingId());
            sendFailureNotification(event);
        } catch (Exception e) {
            log.error("Error handling booking failed notification", e);
        }
    }

    /**
     * Send success notification (Email/SMS simulation)
     */
    private void sendSuccessNotification(PaymentCompletedEvent event) {
        String formattedDate = event.getPaidAt() != null 
            ? event.getPaidAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        String message = String.format(
            "ĐẶT THÀNH CÔNG\n\n" +
            "Mã đặt vé: #%d\n" +
            "Người dùng ID: %d\n" +
            "Số tiền: %,.0f VND\n" +
            "Thời gian: %s\n\n" +
            "Cảm ơn bạn đã sử dụng dịch vụ!",
            event.getBookingId(), event.getUserId(), event.getAmount(), formattedDate
        );

        log.info("Sending SUCCESS notification to User {}\n{}", event.getUserId(), message);
        // Simulate email sending
        sendEmailNotification(event.getUserId(), "Đặt vé thành công", message);
        // Simulate SMS sending
        sendSMSNotification(event.getUserId(), message);
    }

    /**
     * Send failure notification (Email/SMS simulation)
     */
    private void sendFailureNotification(PaymentCompletedEvent event) {
        String message = String.format(
            "ĐẶT VÉ THẤT BẠI\n\n" +
            "Mã đặt vé: #%d\n" +
            "Người dùng ID: %d\n" +
            "Số tiền: %,.0f VND\n\n" +
            "Thanh toán thất bại. Vui lòng kiểm tra lại thông tin và thử lại.",
            event.getBookingId(), event.getUserId(), event.getAmount()
        );

        log.warn("Sending FAILURE notification to User {}\n{}", event.getUserId(), message);
        // Simulate email sending
        sendEmailNotification(event.getUserId(), "Đặt vé thất bại", message);
        // Simulate SMS sending
        sendSMSNotification(event.getUserId(), message);
    }

    /**
     * Simulate email notification
     */
    private void sendEmailNotification(int userId, String subject, String message) {
        // In real application, use JavaMailSender
        log.info("EMAIL SENT");
        log.info("To: user{}@example.com", userId);
        log.info("Subject: {}", subject);
        log.info("Message: {}", message);
    }

    /**
     * Simulate SMS notification
     */
    private void sendSMSNotification(int userId, String message) {
        // In real application, use SMS gateway (Twilio, etc.)
        log.info("SMS SENT");
        log.info("To: User {} (Phone)", userId);
        log.info("Message: {}", message);
    }
}
