package com.ecommerce.orderservice.service;

import lombok.extern.slf4j.Slf4j;

import com.ecommerce.orderservice.client.InventoryServiceClient;
import com.ecommerce.orderservice.client.PaymentServiceClient;
import com.ecommerce.orderservice.dto.*;
import com.ecommerce.shared.event.OrderCreatedEvent;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.model.OrderStatusHistory;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.repository.OrderStatusHistoryRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, OrderStatusHistoryRepository orderStatusHistoryRepository,
                            InventoryServiceClient inventoryServiceClient, PaymentServiceClient paymentServiceClient,
                            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.inventoryServiceClient = inventoryServiceClient;
        this.paymentServiceClient = paymentServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public OrderCreationResponseDTO createOrder(OrderCreationRequestDTO request) {
        // 1. Perform Soft Reservation with Inventory Service
        boolean allItemsReserved = true;
        for (OrderItemRequestDTO item : request.getItems()) {
            SoftReserveRequestDTO softReserveRequest = new SoftReserveRequestDTO(item.getProductId(), item.getQuantity(), request.getUserId());
            SoftReserveResponseDTO softReserveResponse = inventoryServiceClient.softReserve(softReserveRequest);
            if (!softReserveResponse.isSuccess()) {
                allItemsReserved = false;
                log.error("Soft reservation failed for product {}. Reason: {}", item.getProductId(), softReserveResponse.getMessage());
                // In a real system, you might want to rollback already reserved items here
                break;
            }
        }

        if (!allItemsReserved) {
            return new OrderCreationResponseDTO(null, "FAILED", "Some items are out of stock or could not be reserved.");
        }

        // 2. Create Order entity and persist it
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus("PENDING_PAYMENT"); // Initial status

        // Create Order Items
        Order finalOrder = order; // Need a final or effectively final variable for lambda
        List<OrderItem> orderItems = request.getItems().stream()
                .map(itemRequest -> new OrderItem(finalOrder, itemRequest.getProductId(), itemRequest.getQuantity(), itemRequest.getPrice()))
                .collect(Collectors.toList());
        order.setItems(orderItems);

        // Save order and record initial status history
        order = orderRepository.save(order);
        OrderStatusHistory initialStatus = new OrderStatusHistory(order, null, "PENDING_PAYMENT");
        orderStatusHistoryRepository.save(initialStatus);

        // 3. Process Payment
        PaymentRequestDTO paymentRequest = new PaymentRequestDTO(order.getId(), request.getUserId(), request.getTotalAmount(), "USD"); // Assuming USD
        log.info("Sending payment request for orderId: {}", order.getId());
        PaymentResponseDTO paymentResponse = paymentServiceClient.processPayment(paymentRequest);

        if (!paymentResponse.isSuccess()) {
            log.error("Payment failed for orderId: {}. Reason: {}", order.getId(), paymentResponse.getMessage());
            order.setStatus("PAYMENT_FAILED");
            orderRepository.save(order); // Update order status in DB

            // Publish order.payment.failed event
            List<OrderCreatedEvent.OrderItemEvent> itemEvents = order.getItems().stream()
                    .map(item -> new OrderCreatedEvent.OrderItemEvent(item.getProductId(), item.getQuantity()))
                    .collect(Collectors.toList());
            OrderCreatedEvent orderPaymentFailedEvent = new OrderCreatedEvent(order.getId(), itemEvents);
            kafkaTemplate.send("order.payment.failed", order.getId().toString(), orderPaymentFailedEvent);
            log.info("Published order.payment.failed event for orderId: {}", order.getId());

            return new OrderCreationResponseDTO(order.getId(), "PAYMENT_FAILED", paymentResponse.getMessage());
        }

        order.setPaymentId(paymentResponse.getTransactionId());
        order.setStatus("PAID"); // Update status to PAID after successful payment
        orderRepository.save(order); // Save updated status and paymentId

        // Record status history for PAID status
        OrderStatusHistory paidStatus = new OrderStatusHistory(order, "PENDING_PAYMENT", "PAID");
        orderStatusHistoryRepository.save(paidStatus);

        // Publish order.created event
        List<OrderCreatedEvent.OrderItemEvent> itemEvents = order.getItems().stream()
                .map(item -> new OrderCreatedEvent.OrderItemEvent(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(order.getId(), itemEvents);
        kafkaTemplate.send("order.created", order.getId().toString(), orderCreatedEvent);
        log.info("Published order.created event for orderId: {}", order.getId());

        return new OrderCreationResponseDTO(order.getId(), order.getStatus(), "Order created successfully, pending payment.");
    }

    @Transactional(readOnly = true)
    public OrderDetailsDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id)); // Custom exception recommended

        return convertToOrderDetailsDTO(order);
    }

    @Transactional
    public OrderDetailsDTO cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id)); // Custom exception recommended

        // Validate if the order can be cancelled
        if (!"PENDING_PAYMENT".equals(order.getStatus()) && !"PAID".equals(order.getStatus())) {
            throw new RuntimeException("Order cannot be cancelled in status: " + order.getStatus()); // Custom exception recommended
        }

        String oldStatus = order.getStatus();
        order.setStatus("CANCELLED");
        order = orderRepository.save(order);

        // Record status history
        OrderStatusHistory cancellationStatus = new OrderStatusHistory(order, oldStatus, "CANCELLED");
        orderStatusHistoryRepository.save(cancellationStatus);

        // In a real system, publish an order.cancelled event here for Inventory Service to rollback hard reservation
        // and for Notification Service to send cancellation confirmation.

        return convertToOrderDetailsDTO(order);
    }

    private OrderDetailsDTO convertToOrderDetailsDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderItemDTO(item.getProductId(), item.getQuantity(), item.getPrice()))
                .collect(Collectors.toList());

        List<OrderStatusHistoryDTO> historyDTOs = order.getStatusHistory().stream()
                .map(history -> new OrderStatusHistoryDTO(history.getOldStatus(), history.getNewStatus(), history.getChangedAt()))
                .collect(Collectors.toList());

        return new OrderDetailsDTO(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentId(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                itemDTOs,
                historyDTOs
        );
    }
    @Transactional
    public OrderDetailsDTO processPaymentCallback(Long orderId, PaymentResponseDTO paymentResponse) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        String oldStatus = order.getStatus();
        if (paymentResponse.isSuccess()) {
            order.setStatus("PAID");
            order.setPaymentId(paymentResponse.getTransactionId());
            log.info("Order {} status updated to PAID. Transaction ID: {}", orderId, paymentResponse.getTransactionId());
        } else {
            order.setStatus("PAYMENT_FAILED");
            log.warn("Order {} payment failed. Reason: {}", orderId, paymentResponse.getMessage());
            // Publish order.payment.failed event
            List<OrderCreatedEvent.OrderItemEvent> itemEvents = order.getItems().stream()
                    .map(item -> new OrderCreatedEvent.OrderItemEvent(item.getProductId(), item.getQuantity()))
                    .collect(Collectors.toList());
            OrderCreatedEvent orderPaymentFailedEvent = new OrderCreatedEvent(order.getId(), itemEvents);
            kafkaTemplate.send("order.payment.failed", order.getId().toString(), orderPaymentFailedEvent);
            log.info("Published order.payment.failed event for orderId: {}", order.getId());
        }
        order = orderRepository.save(order);

        OrderStatusHistory statusHistory = new OrderStatusHistory(order, oldStatus, order.getStatus());
        orderStatusHistoryRepository.save(statusHistory);

        // In a real system, you might want to publish an event here (e.g., order.paid, order.paymentFailed)
        // for other services to react (e.g., Inventory Service to hard reserve, Notification Service to send confirmation)

        return convertToOrderDetailsDTO(order);
    }
}