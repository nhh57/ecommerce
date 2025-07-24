package com.ecommerce.inventoryservice.dto;

public class SoftReserveRequestDTO {
    private Long productId;
    private int quantity;
    private Long orderId; // Associated order ID for logging/tracking

    // Constructors
    public SoftReserveRequestDTO() {
    }

    public SoftReserveRequestDTO(Long productId, int quantity, Long orderId) {
        this.productId = productId;
        this.quantity = quantity;
        this.orderId = orderId;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}