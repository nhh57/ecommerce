package com.ecommerce.inventoryservice.listener;

import com.ecommerce.inventoryservice.event.OrderPaymentFailedEvent;
import com.ecommerce.inventoryservice.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderPaymentFailedListener {

    private final InventoryService inventoryService;

    public OrderPaymentFailedListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = "order.payment.failed", groupId = "inventory-service-payment-failed-consumer")
    public void listen(OrderPaymentFailedEvent event) {
        log.info("Received OrderPaymentFailedEvent: {}", event);
        event.getItems().forEach(item -> {
            inventoryService.rollbackSoftReservation(item.getProductId(), item.getQuantity(), event.getOrderId());
        });
    }
}