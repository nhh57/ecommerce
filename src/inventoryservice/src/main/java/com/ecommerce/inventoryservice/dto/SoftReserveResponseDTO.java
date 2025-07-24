package com.ecommerce.inventoryservice.dto;

public class SoftReserveResponseDTO {
    private boolean success;
    private String message;
    private int currentAvailableQuantity; // Optional, for client info

    // Constructors
    public SoftReserveResponseDTO() {
    }

    public SoftReserveResponseDTO(boolean success, String message, int currentAvailableQuantity) {
        this.success = success;
        this.message = message;
        this.currentAvailableQuantity = currentAvailableQuantity;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCurrentAvailableQuantity() {
        return currentAvailableQuantity;
    }

    public void setCurrentAvailableQuantity(int currentAvailableQuantity) {
        this.currentAvailableQuantity = currentAvailableQuantity;
    }
}