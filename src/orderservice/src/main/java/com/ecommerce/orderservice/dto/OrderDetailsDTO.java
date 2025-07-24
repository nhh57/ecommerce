package com.ecommerce.orderservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDetailsDTO {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private String paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> items; // Nested DTO for order items
    private List<OrderStatusHistoryDTO> statusHistory; // Nested DTO for status history

    // Constructors
    public OrderDetailsDTO() {
    }

    public OrderDetailsDTO(Long id, Long userId, BigDecimal totalAmount, String status, String paymentId, LocalDateTime createdAt, LocalDateTime updatedAt, List<OrderItemDTO> items, List<OrderStatusHistoryDTO> statusHistory) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentId = paymentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.items = items;
        this.statusHistory = statusHistory;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public List<OrderStatusHistoryDTO> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<OrderStatusHistoryDTO> statusHistory) {
        this.statusHistory = statusHistory;
    }
}