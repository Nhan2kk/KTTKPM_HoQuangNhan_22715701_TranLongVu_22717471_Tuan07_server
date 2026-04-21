package fit.se.movieservice.config;


import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE = "movie_exchange";
    public static final String QUEUE = "movie_queue";
    public static final String ROUTING_KEY = "movie_routing_key";

    @Bean
    public TopicExchange movieExchange() { return new TopicExchange(EXCHANGE); }

    @Bean
    public Queue movieQueue() { return new Queue(QUEUE); }

    @Bean
    public Binding movieBinding(Queue movieQueue, TopicExchange movieExchange) {
        return BindingBuilder.bind(movieQueue).to(movieExchange).with(ROUTING_KEY);
    }

    // Cách viết để hỗ trợ cả LocalDate mà không bị lỗi
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // Thêm "*" để tin tưởng tất cả các package khi deserialize dữ liệu
        return new Jackson2JsonMessageConverter(objectMapper, "*");
    }
}