package com.ecommerce.inventoryservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_logs")
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String changeType; // e.g., SOFT_RESERVE, HARD_RESERVE, REFUND

    @Column(nullable = false)
    private int quantityChange;

    @Column(nullable = false)
    private int newQuantity;

    private Long orderId; // Associated Order ID, can be null

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Constructors
    public InventoryLog() {
    }

    public InventoryLog(Long productId, String changeType, int quantityChange, int newQuantity, Long orderId) {
        this.productId = productId;
        this.changeType = changeType;
        this.quantityChange = quantityChange;
        this.newQuantity = newQuantity;
        this.orderId = orderId;
    }

    // Getters and Setters
    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public int getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(int quantityChange) {
        this.quantityChange = quantityChange;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(int newQuantity) {
        this.newQuantity = newQuantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}