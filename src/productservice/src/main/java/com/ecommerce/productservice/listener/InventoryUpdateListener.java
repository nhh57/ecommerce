package com.ecommerce.productservice.listener;

import com.ecommerce.productservice.event.InventoryUpdatedEvent;
import com.ecommerce.productservice.service.ProductService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
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