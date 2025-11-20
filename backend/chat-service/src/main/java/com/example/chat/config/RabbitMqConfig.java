package com.example.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.util.UUID;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {
    public static final String TOPIC_EXCHANGE = "chat.topic.exchange";
    public static final String PRIVATE_QUEUE = "chat.private.instance.queue";
    private final AmqpAdmin amqpAdmin;

    @Bean
    public Queue queue() {
        return new Queue(PRIVATE_QUEUE, true, false,false); // durable queue
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange (TOPIC_EXCHANGE);
    }

//    @Bean
//    public Binding binding(Queue queue, TopicExchange  exchange)
//    {
//        return BindingBuilder.bind(queue)
//                .to(exchange)
//                .with("#");
//    }

    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(2)
                .backOffOptions(1000, 2.0, 10000) // initialInterval, multiplier, maxInterval
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }

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

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, RetryOperationsInterceptor retryInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        factory.setAdviceChain(retryInterceptor);

        return factory;

    }
}
