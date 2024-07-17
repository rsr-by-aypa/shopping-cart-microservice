package com.rsr.shopping_cart_microservice.core.port.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.product.created.binding_key}")
    private String productCreatedBindingKey;

    @Value("${rabbitmq.product.updated.binding_key}")
    private String productUpdatedBindingKey;

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.product.created.queue.name}")
    private String productCreatedQueue;

    @Value("${rabbitmq.product.updated.queue.name}")
    private String productUpdatedQueue;

    @Value("${rabbitmq.order.created.queue.name}")
    private String orderCreatedQueue;

    @Value("${rabbitmq.order.created.binding_key}")
    private String orderCreatedBindingKey;

    @Bean
    public Queue productCreatedQueue() {
        return new Queue(productCreatedQueue);
    }

    @Bean
    public Queue productUpdatedQueue() {
        return new Queue(productUpdatedQueue);
    }

    @Bean
    public Queue orderCreatedQueue() { return new Queue(orderCreatedQueue); }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding productCreatedBinding() {
        return BindingBuilder.bind(productCreatedQueue()).to(exchange()).with(productCreatedBindingKey);
    }

    @Bean
    public Binding productUpdatedBinding() {
        return BindingBuilder.bind(productUpdatedQueue()).to(exchange()).with(productUpdatedBindingKey);
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder
                .bind(orderCreatedQueue())
                .to(exchange())
                .with(orderCreatedBindingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
