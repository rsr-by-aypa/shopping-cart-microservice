package com.rsr.shopping_cart_microservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class ShoppingCartMicroserviceApplicationTests {

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

	@Test
	void contextLoads() {
	}

}
