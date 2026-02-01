package com.example.common;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class SharedRabbitTopology {
    // AI Service Topology
    public static final String AI_EXCHANGE = "ai.exchange";
    public static final String QUEUE_GENERATE_NAME = "ai.generate.queue";
    public static final String GENERATE_ROUTING_KEY = "ai.generate";

    public static final String QUEUE_RESPONSE_NAME = "ai.response.queue";
    public static final String RESPONSE_ROUTING_KEY = "ai.response";

    @Bean
    public TopicExchange aiTopicExchange() {
        return new TopicExchange(AI_EXCHANGE);
    }

    @Bean
    public Queue aiGenerateQueue() {
        return new Queue(QUEUE_GENERATE_NAME, true);
    }

    @Bean
    public Binding binding(Queue aiGenerateQueue, TopicExchange aiTopicExchange) {
        return BindingBuilder
                .bind(aiGenerateQueue)
                .to(aiTopicExchange)
                .with(GENERATE_ROUTING_KEY);
    }

    @Bean
    public Queue aiResponseQueue() {
        return new Queue(QUEUE_RESPONSE_NAME, true);
    }

    @Bean
    public Binding responseBinding(Queue aiResponseQueue, TopicExchange aiTopicExchange) {
        return BindingBuilder.bind(aiResponseQueue).to(aiTopicExchange).with(RESPONSE_ROUTING_KEY);
    }


    // Chat Service Topology
    public static final String TOPIC_EXCHANGE = "chat.topic.exchange";

    public static final String USER_DELETED_QUEUE = "chat.user-deleted.queue";
    public static final String USER_DELETED_ROUTING_KEY = "user.deleted";
    @Bean
    public Queue instanceQueue() {
        return new Queue("chat.private" + UUID.randomUUID(), false, true,true);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange (TOPIC_EXCHANGE);
    }

    // User Deleted Queue and Binding
    @Bean
    public Queue userDeletedQueue() {
        return new Queue(USER_DELETED_QUEUE, true, false, false);
    }

    @Bean
    public Binding userDeletedBinding(Queue userDeletedQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(userDeletedQueue)
                .to(topicExchange)
                .with(USER_DELETED_ROUTING_KEY);
    }
}
