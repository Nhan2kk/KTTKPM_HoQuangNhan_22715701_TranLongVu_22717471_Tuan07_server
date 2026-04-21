package fit.se.bookingservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // Exchange Names
    public static final String BOOKING_EXCHANGE = "booking.exchange";

    // Queue Names
    public static final String BOOKING_CREATED_QUEUE = "booking.created.queue";
    public static final String PAYMENT_QUEUE = "payment.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    // Routing Keys
    public static final String BOOKING_CREATED_ROUTING_KEY = "booking.created";
    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed";
    public static final String BOOKING_FAILED_ROUTING_KEY = "booking.failed";

    // ==================== EXCHANGES ====================
    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(BOOKING_EXCHANGE, true, false);
    }

    // ==================== QUEUES ====================
    @Bean
    public Queue bookingCreatedQueue() {
        return new Queue(BOOKING_CREATED_QUEUE, true);
    }

    @Bean
    public Queue paymentQueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    // ==================== BINDINGS ====================
    @Bean
    public Binding bookingCreatedBinding(Queue bookingCreatedQueue, TopicExchange bookingExchange) {
        return BindingBuilder
                .bind(bookingCreatedQueue)
                .to(bookingExchange)
                .with(BOOKING_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding paymentBinding(Queue paymentQueue, TopicExchange bookingExchange) {
        return BindingBuilder
                .bind(paymentQueue)
                .to(bookingExchange)
                .with(BOOKING_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange bookingExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(bookingExchange)
                .with(PAYMENT_COMPLETED_ROUTING_KEY + ",booking.failed");
    }
}
