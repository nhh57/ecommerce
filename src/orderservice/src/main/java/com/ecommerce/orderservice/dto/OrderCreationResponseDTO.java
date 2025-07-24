package com.ecommerce.orderservice.dto;

public class OrderCreationResponseDTO {
    private String status;
    private Long orderId;

    public OrderCreationResponseDTO() {
    }

    public OrderCreationResponseDTO(String status, Long orderId) {
        this.status = status;
        this.orderId = orderId;
    }

    public OrderCreationResponseDTO(Long orderId, String status, String message) {
        this.orderId = orderId;
        this.status = status;
        // You might want to add a 'message' field to the DTO if it's consistently used
        // For now, it's not part of the DTO's properties, so we'll ignore it or log it
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}