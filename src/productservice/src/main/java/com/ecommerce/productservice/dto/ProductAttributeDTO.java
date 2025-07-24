package com.ecommerce.productservice.dto;

public class ProductAttributeDTO {
    private String attributeName;
    private String attributeValue;

    // Constructors
    public ProductAttributeDTO() {
    }

    public ProductAttributeDTO(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    // Getters and Setters
    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
}