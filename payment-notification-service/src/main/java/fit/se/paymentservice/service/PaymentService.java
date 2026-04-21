package fit.se.paymentservice.service;

import fit.se.paymentservice.config.RabbitMQConfig;
import fit.se.paymentservice.entity.Payment;
import fit.se.paymentservice.event.BookingCreatedEvent;
import fit.se.paymentservice.event.PaymentCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Listen to BOOKING_CREATED event from Booking Service
     * Process payment and publish PAYMENT_COMPLETED event
     */
    @RabbitListener(queues = RabbitMQConfig.BOOKING_CREATED_QUEUE)
    public void processBookingPayment(BookingCreatedEvent event) {
        try {
            log.info("Received BOOKING_CREATED event - Booking ID: {}, Amount: {}", 
                    event.getBookingId(), event.getTotalPrice());

            // Create payment record
            Payment payment = new Payment();
            payment.setBookingId(event.getBookingId());
            payment.setUserId(event.getUserId());
            payment.setAmount(event.getTotalPrice());
            
            // Simulate payment processing (70% success rate)
            boolean paymentSuccess = new Random().nextDouble() < 0.7;
            
            if (paymentSuccess) {
                payment.setStatus(Payment.PaymentStatus.SUCCESS);
                payment.setPaidAt(LocalDateTime.now());
                log.info("Payment processed successfully for Booking ID: {}", event.getBookingId());
            } else {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                log.warn("Payment failed for Booking ID: {}", event.getBookingId());
            }

            Payment savedPayment = paymentRepository.save(payment);

            // Publish PAYMENT_COMPLETED event
            publishPaymentCompletedEvent(savedPayment, event.getUserId());

        } catch (Exception e) {
            log.error("Error processing payment for booking ID: {}", event.getBookingId(), e);
        }
    }

    /**
     * Publish PAYMENT_COMPLETED event to Notification Service
     */
    private void publishPaymentCompletedEvent(Payment payment, int userId) {
        try {
            PaymentCompletedEvent event = new PaymentCompletedEvent();
            event.setBookingId(payment.getBookingId());
            event.setUserId(userId);
            event.setPaymentId(payment.getId());
            event.setAmount(payment.getAmount());
            event.setStatus(payment.getStatus().toString());
            event.setPaidAt(payment.getPaidAt());
            event.setEventTimestamp(LocalDateTime.now().toString());

            log.info("Publishing PAYMENT_COMPLETED event - Booking ID: {}, Status: {}", 
                    payment.getBookingId(), payment.getStatus());
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.BOOKING_EXCHANGE,
                    RabbitMQConfig.PAYMENT_COMPLETED_ROUTING_KEY,
                    event
            );

            log.info("Event published successfully!");
        } catch (Exception e) {
            log.error("Failed to publish PAYMENT_COMPLETED event", e);
        }
    }

    public List<Payment> getPaymentsByBookingId(int bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    public List<Payment> getPaymentsByUserId(int userId) {
        return paymentRepository.findByUserId(userId);
    }
}
