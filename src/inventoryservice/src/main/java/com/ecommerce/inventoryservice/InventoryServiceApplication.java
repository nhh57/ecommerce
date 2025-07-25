package com.ecommerce.inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.data.redis.core.RedisTemplate;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import com.ecommerce.inventoryservice.model.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableKafka
public class InventoryServiceApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadInventoryData(InventoryRepository inventoryRepository, RedisTemplate<String, Object> redisTemplate) {
		return args -> {
			inventoryRepository.findAll().forEach(inventory -> {
				String redisKeyAvailable = "inventory:" + inventory.getProductId() + ":available";
				String redisKeyReserved = "inventory:" + inventory.getProductId() + ":reserved";
				redisTemplate.opsForValue().set(redisKeyAvailable, inventory.getAvailableQuantity());
				redisTemplate.opsForValue().set(redisKeyReserved, inventory.getReservedQuantity());
				LOGGER.info("Loaded inventory for productId {} into Redis. Available: {}, Reserved: {}",
						inventory.getProductId(), inventory.getAvailableQuantity(), inventory.getReservedQuantity());
			});
		};
	}

}