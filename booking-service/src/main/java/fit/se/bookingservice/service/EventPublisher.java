package fit.se.bookingservice.service;

import fit.se.bookingservice.config.RabbitMQConfig;
import fit.se.bookingservice.event.BookingCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Publish BOOKING_CREATED event to Payment Service and Notification Service
     */
    public void publishBookingCreatedEvent(BookingCreatedEvent event) {
        try {
            log.info("Publishing BOOKING_CREATED event for booking ID: {}", event.getBookingId());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.BOOKING_EXCHANGE,
                    RabbitMQConfig.BOOKING_CREATED_ROUTING_KEY,
                    event
            );
            log.info("Event published successfully! Booking ID: {}", event.getBookingId());
        } catch (Exception e) {
            log.error("Failed to publish BOOKING_CREATED event for booking ID: {}", event.getBookingId(), e);
            throw new RuntimeException("Failed to publish booking created event", e);
        }
    }
}
