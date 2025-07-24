package com.ecommerce.orderservice.dto;

import java.math.BigDecimal;

public class PaymentRequestDTO {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String currency;

    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(Long orderId, Long userId, BigDecimal amount, String currency) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}