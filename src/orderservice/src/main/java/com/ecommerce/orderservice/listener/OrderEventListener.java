package com.ecommerce.orderservice.listener;

import com.ecommerce.shared.event.OrderCreatedEvent; // Example event, might need other event DTOs
import com.ecommerce.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {

    private final OrderService orderService;

    public OrderEventListener(OrderService orderService) {
        this.orderService = orderService;
    }

    // Example listener for a refund success event
    @KafkaListener(topics = "order.refund.succeeded", groupId = "order-service-refund-consumer")
    public void listenRefundSucceeded(OrderCreatedEvent event) { // Using OrderCreatedEvent as a placeholder for now
        log.info("Received order.refund.succeeded event for orderId: {}", event.getOrderId());
        // In a real scenario, you would update the order status to REFUNDED
        // orderService.updateOrderStatus(event.getOrderId(), "REFUNDED");
    }

    // Example listener for a refund failed event
    @KafkaListener(topics = "order.refund.failed", groupId = "order-service-refund-consumer")
    public void listenRefundFailed(OrderCreatedEvent event) { // Using OrderCreatedEvent as a placeholder for now
        log.warn("Received order.refund.failed event for orderId: {}", event.getOrderId());
        // In a real scenario, you would update the order status to REFUND_FAILED
        // orderService.updateOrderStatus(event.getOrderId(), "REFUND_FAILED");
    }
}