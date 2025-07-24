package com.ecommerce.inventoryservice.listener;

import com.ecommerce.inventoryservice.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;

import com.ecommerce.inventoryservice.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCreatedListener {

    private final InventoryService inventoryService;

    public OrderCreatedListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "order.created", groupId = "inventory-service-order-consumer")
    public void listen(OrderCreatedEvent event) { // Change type to OrderCreatedEvent from orderservice package
        System.out.println("Received OrderCreatedEvent: " + event);
        // Assuming OrderCreatedEvent contains a list of order items
        event.getItems().forEach(item -> {
            inventoryService.hardReserveInventory(item.getProductId(), item.getQuantity(), event.getOrderId());
        });
    }
}