package fit.se.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // Exchange Names
    public static final String BOOKING_EXCHANGE = "booking.exchange";

    // Queue Names - LISTEN TO PAYMENT_COMPLETED and BOOKING_FAILED
    public static final String PAYMENT_COMPLETED_QUEUE = "notification.payment.completed.queue";
    public static final String BOOKING_FAILED_QUEUE = "notification.booking.failed.queue";

    // Routing Keys
    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed";
    public static final String BOOKING_FAILED_ROUTING_KEY = "booking.failed";

    // ==================== EXCHANGES ====================
    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(BOOKING_EXCHANGE, true, false);
    }

    // ==================== QUEUES ====================
    @Bean
    public Queue paymentCompletedQueue() {
        return new Queue(PAYMENT_COMPLETED_QUEUE, true);
    }

    @Bean
    public Queue bookingFailedQueue() {
        return new Queue(BOOKING_FAILED_QUEUE, true);
    }

    // ==================== BINDINGS ====================
    @Bean
    public Binding paymentCompletedBinding(Queue paymentCompletedQueue, TopicExchange bookingExchange) {
        return BindingBuilder
                .bind(paymentCompletedQueue)
                .to(bookingExchange)
                .with(PAYMENT_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Binding bookingFailedBinding(Queue bookingFailedQueue, TopicExchange bookingExchange) {
        return BindingBuilder
                .bind(bookingFailedQueue)
                .to(bookingExchange)
                .with(BOOKING_FAILED_ROUTING_KEY);
    }
}
