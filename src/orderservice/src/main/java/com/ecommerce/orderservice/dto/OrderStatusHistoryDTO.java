package com.ecommerce.orderservice.dto;

import java.time.LocalDateTime;

public class OrderStatusHistoryDTO {
    private String oldStatus;
    private String newStatus;
    private LocalDateTime changedAt;

    // Constructors
    public OrderStatusHistoryDTO() {
    }

    public OrderStatusHistoryDTO(String oldStatus, String newStatus, LocalDateTime changedAt) {
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
    }

    // Getters and Setters
    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}