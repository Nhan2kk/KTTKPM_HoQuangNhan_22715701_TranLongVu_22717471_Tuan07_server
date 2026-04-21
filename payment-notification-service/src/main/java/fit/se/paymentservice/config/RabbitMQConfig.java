package fit.se.paymentservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // Exchange Names
    public static final String BOOKING_EXCHANGE = "booking.exchange";

    // Queue Names - LISTEN TO BOOKING_CREATED
    public static final String BOOKING_CREATED_QUEUE = "payment.booking.created.queue";

    // Queue Names - PUBLISH PAYMENT_COMPLETED
    public static final String PAYMENT_COMPLETED_QUEUE = "payment.completed.queue";

    // Routing Keys
    public static final String BOOKING_CREATED_ROUTING_KEY = "booking.created";
    public static final String PAYMENT_COMPLETED_ROUTING_KEY = "payment.completed";

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
    public Queue paymentCompletedQueue() {
        return new Queue(PAYMENT_COMPLETED_QUEUE, true);
    }

    // ==================== BINDINGS ====================
    @Bean
    public Binding bookingCreatedBinding(Queue bookingCreatedQueue, TopicExchange bookingExchange) {
        return BindingBuilder
                .bind(bookingCreatedQueue)
                .to(bookingExchange)
                .with(BOOKING_CREATED_ROUTING_KEY);
    }

    // ==================== MESSAGE CONVERTER ====================
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }
}
