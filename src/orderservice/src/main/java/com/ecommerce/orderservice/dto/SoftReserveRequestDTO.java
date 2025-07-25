package com.ecommerce.orderservice.dto;

import lombok.ToString;

@ToString
public class SoftReserveRequestDTO {
    private Long productId;
    private Integer quantity;
    private Long userId;

    public SoftReserveRequestDTO() {
    }

    public SoftReserveRequestDTO(Long productId, Integer quantity, Long userId) {
        this.productId = productId;
        this.quantity = quantity;
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}