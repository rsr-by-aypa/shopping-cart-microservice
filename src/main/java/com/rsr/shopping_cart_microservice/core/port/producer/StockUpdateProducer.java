package com.rsr.shopping_cart_microservice.core.port.producer;

import com.rsr.shopping_cart_microservice.core.port.dto.ProductDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StockUpdateProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockUpdateProducer.class);

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.amount_change.routing_key}")
    private String stockUpdateRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public StockUpdateProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendStockUpdate(ProductDTO productDTO) {
        LOGGER.info("Sending stock update: " + productDTO.toString());
        rabbitTemplate.convertAndSend(exchange, stockUpdateRoutingKey, productDTO);
    }
}
