package com.rsr.shopping_cart_microservice.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsr.shopping_cart_microservice.core.domain.model.Item;
import com.rsr.shopping_cart_microservice.core.domain.model.Product;
import com.rsr.shopping_cart_microservice.core.domain.model.ShoppingCart;
import com.rsr.shopping_cart_microservice.core.domain.service.impl.ShoppingCartService;
import com.rsr.shopping_cart_microservice.core.domain.service.interfaces.IProductRepository;
import com.rsr.shopping_cart_microservice.core.domain.service.interfaces.IShoppingCartRepository;
import com.rsr.shopping_cart_microservice.core.port.dto.CartItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ShoppingCartControllerTest {

    @Container
    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.13.3");

    @Container
    private static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        String dbUrl = postgresqlContainer.getJdbcUrl();
        String username = postgresqlContainer.getUsername();
        String password = postgresqlContainer.getPassword();

        registry.add("spring.datasource.url",
                () -> dbUrl);
        registry.add("spring.datasource.username",
                () -> username);
        registry.add("spring.datasource.password",
                () -> password);

        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loadContext() {
        // Context load test
    }

    UUID productId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID shoppingCartId;

    @Autowired
    IProductRepository productRepository;

    @Autowired
    IShoppingCartRepository shoppingCartRepository;

    @BeforeEach
    void setUp() {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            Product product = new Product();
            product.setId(productId);
            product.setNumberInStock(40);
            productRepository.save(product);
        }

        Item item = new Item();
        item.setProductId(productId);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId);
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
            shoppingCart.setItems(List.of(item));
            shoppingCartRepository.save(shoppingCart);
        }
    }

    @Test
    void addItemToCartTest() throws Exception {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(productId);
        cartItemDTO.setAmount(2);

        mockMvc.perform(post("/shopping-cart/add/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Item added to cart"));
    }

    @Test
    void updateItemInCartTest() throws Exception {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(productId);
        cartItemDTO.setAmount(5);

        mockMvc.perform(post("/shopping-cart/update/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Item quantity updated in cart"));
    }

    /*
    @Test
    void removeItemFromCartTest() throws Exception {
        mockMvc.perform(delete("/shopping-cart/remove/{productId}/{userId}", userId, productId))
                .andExpect(status().isOk())
                .andExpect(content().string("Item removed from cart"));
    }

     */

    @Test
    void getCartByUserIdTest() throws Exception {

        mockMvc.perform(get("/shopping-cart/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void deleteCartTest() throws Exception {
        mockMvc.perform(delete("/shopping-cart/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart deleted"));
    }
}
