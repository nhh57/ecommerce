package com.ecommerce.productservice.event;

public class InventoryUpdatedEvent {
    private Long productId;
    private int newQuantity;

    // Constructors
    public InventoryUpdatedEvent() {
    }

    public InventoryUpdatedEvent(Long productId, int newQuantity) {
        this.productId = productId;
        this.newQuantity = newQuantity;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(int newQuantity) {
        this.newQuantity = newQuantity;
    }

    @Override
    public String toString() {
        return "InventoryUpdatedEvent{" +
               "productId=" + productId +
               ", newQuantity=" + newQuantity +
               '}';
    }
}