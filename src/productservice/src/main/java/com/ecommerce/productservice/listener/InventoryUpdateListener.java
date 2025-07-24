package com.ecommerce.productservice.listener;

import lombok.extern.slf4j.Slf4j;

import com.ecommerce.productservice.event.InventoryUpdatedEvent;
import com.ecommerce.productservice.service.ProductService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InventoryUpdateListener {

    private final ProductService productService;

    public InventoryUpdateListener(ProductService productService) {
        this.productService = productService;
    }

    @KafkaListener(topics = "inventory.updated", groupId = "product-service-group")
    public void listen(InventoryUpdatedEvent event) {
        System.out.println("Received InventoryUpdatedEvent: " + event);
        productService.updateProductDisplayStatus(event.getProductId(), event.getNewQuantity());
    }
}