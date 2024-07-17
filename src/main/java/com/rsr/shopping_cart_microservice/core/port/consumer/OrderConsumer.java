package com.rsr.shopping_cart_microservice.core.port.consumer;

import com.rsr.shopping_cart_microservice.core.domain.service.interfaces.IProductService;
import com.rsr.shopping_cart_microservice.core.domain.service.interfaces.IShoppingCartRepository;
import com.rsr.shopping_cart_microservice.core.domain.service.interfaces.IShoppingCartService;
import com.rsr.shopping_cart_microservice.core.port.dto.OrderCreatedDto;
import com.rsr.shopping_cart_microservice.core.port.dto.ProductDTO;
import com.rsr.shopping_cart_microservice.utils.exceptions.NoCartException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class OrderConsumer {

    @Autowired
    private IShoppingCartService shoppingCartService;

    @RabbitListener(queues = "${rabbitmq.order.created.queue.name}")
    public void handleOrderCreated(OrderCreatedDto orderCreatedDto) {
        log.info("Received Order {}", orderCreatedDto);
        try {
            shoppingCartService.deleteCart(orderCreatedDto.getUserId());
        } catch (NoCartException e) {
            log.info("Cart already deleted");
        }
    }
}
