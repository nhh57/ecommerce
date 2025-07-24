package com.ecommerce.orderservice.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderCreationRequestDTO {
    private Long userId;
    private List<OrderItemRequestDTO> items;
    private BigDecimal totalAmount; // Should be calculated on backend for security

    // Constructors
    public OrderCreationRequestDTO() {
    }

    public OrderCreationRequestDTO(Long userId, List<OrderItemRequestDTO> items, BigDecimal totalAmount) {
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}