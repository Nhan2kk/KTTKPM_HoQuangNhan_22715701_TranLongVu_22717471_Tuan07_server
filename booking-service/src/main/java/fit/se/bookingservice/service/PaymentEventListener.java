package fit.se.bookingservice.service;

import fit.se.bookingservice.config.RabbitMQConfig;
import fit.se.bookingservice.entity.Booking;
import fit.se.bookingservice.event.PaymentCompletedEvent;
import fit.se.bookingservice.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
public class PaymentEventListener {

    @Autowired
    private BookingRepository bookingRepository;

    /**
     * Listen to PAYMENT_COMPLETED event from Payment Service
     * Update booking status based on payment result
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_COMPLETED_QUEUE)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        updateBookingStatusFromPaymentEvent(event);
    }

    @RabbitListener(queues = RabbitMQConfig.BOOKING_FAILED_QUEUE)
    public void handleBookingFailed(PaymentCompletedEvent event) {
        updateBookingStatusFromPaymentEvent(event);
    }

    private void updateBookingStatusFromPaymentEvent(PaymentCompletedEvent event) {
        try {
            log.info("Received PAYMENT_COMPLETED event - Booking ID: {}, Status: {}", 
                    event.getBookingId(), event.getStatus());

            Booking booking = bookingRepository.findById(event.getBookingId())
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + event.getBookingId()));

            if ("SUCCESS".equals(event.getStatus())) {
                booking.setStatus(Booking.BookingStatus.CONFIRMED);
                log.info("✅ Booking confirmed! ID: {}", event.getBookingId());
            } else {
                booking.setStatus(Booking.BookingStatus.FAILED);
                log.warn("❌ Booking failed due to payment failure! ID: {}", event.getBookingId());
            }

            bookingRepository.save(booking);
            log.info("Booking status updated successfully!");

        } catch (Exception e) {
            log.error("Error handling payment completed event for booking: {}", event.getBookingId(), e);
        }
    }
}
