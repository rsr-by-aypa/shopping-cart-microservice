package com.rsr.shopping_cart_microservice.core.port.consumer;

import com.rsr.shopping_cart_microservice.core.domain.model.Product;
import com.rsr.shopping_cart_microservice.core.domain.service.interfaces.IProductService;
import com.rsr.shopping_cart_microservice.core.port.dto.ProductDTO;
import com.rsr.shopping_cart_microservice.utils.exceptions.ProductIdAlreadyInUseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductConsumer.class);

    @Autowired
    private IProductService productService;

    @RabbitListener(queues = {"product.created.queue"})
    public void handleProductCreated(ProductDTO productDTO) {
        try {
            Product product = new Product(
                    productDTO.getId(),
                    productDTO.getName(),
                    productDTO.getPriceInEuro(),
                    productDTO.getNumberInStock(),
                    productDTO.getImageLink()
            );
            productService.addCreatedProduct(product);
            LOGGER.info("Product created: " + productDTO.toString());
        } catch (ProductIdAlreadyInUseException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.product.updated.queue.name}")
    public void handleProductUpdated(ProductDTO productDTO) {
        try {
            Product product = new Product(
                    productDTO.getId(),
                    productDTO.getName(),
                    productDTO.getPriceInEuro(),
                    productDTO.getNumberInStock(),
                    productDTO.getImageLink()
            );
            productService.updateProduct(product);
            LOGGER.info("Product updated: " + productDTO.toString());
        } catch (ProductIdAlreadyInUseException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
