package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.client.InventoryServiceClient;
import com.ecommerce.orderservice.dto.*;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.model.OrderStatusHistory;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.repository.OrderStatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final InventoryServiceClient inventoryServiceClient;

    public OrderService(OrderRepository orderRepository, OrderStatusHistoryRepository orderStatusHistoryRepository, InventoryServiceClient inventoryServiceClient) {
        this.orderRepository = orderRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @Transactional
    public OrderCreationResponseDTO createOrder(OrderCreationRequestDTO request) {
        // 1. Perform Soft Reservation with Inventory Service
        // For simplicity, assuming total amount is calculated on client, but should be verified on backend
        // Also assuming product prices are validated against a Product Service
        boolean allItemsReserved = true;
        for (OrderItemRequestDTO item : request.getItems()) {
            SoftReserveRequestDTO softReserveRequest = new SoftReserveRequestDTO(item.getProductId(), item.getQuantity(), request.getUserId());
            SoftReserveResponseDTO softReserveResponse = inventoryServiceClient.softReserve(softReserveRequest);
            if (!softReserveResponse.isSuccess()) {
                allItemsReserved = false;
                // Handle failure (e.g., log, throw exception, or return specific error)
                // For now, if any item fails, the whole order creation fails
                // In a real system, you might want to rollback already reserved items
                break;
            }
        }

        if (!allItemsReserved) {
            // In a real system, would need to trigger rollback for already reserved items
            return new OrderCreationResponseDTO(null, "FAILED", "Some items are out of stock or could not be reserved.");
        }

        // 2. Create Order entity
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus("PENDING_PAYMENT"); // Initial status

        // Create Order Items
        Order finalOrder = order;
        List<OrderItem> orderItems = request.getItems().stream()
                .map(itemRequest -> new OrderItem(finalOrder, itemRequest.getProductId(), itemRequest.getQuantity(), itemRequest.getPrice()))
                .collect(Collectors.toList());
        order.setItems(orderItems);

        // Save order
        order = orderRepository.save(order);

        // Record status history
        OrderStatusHistory initialStatus = new OrderStatusHistory(order, null, "PENDING_PAYMENT");
        orderStatusHistoryRepository.save(initialStatus);

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
}