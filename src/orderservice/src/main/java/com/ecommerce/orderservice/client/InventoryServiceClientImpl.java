package com.ecommerce.orderservice.client;

import lombok.extern.slf4j.Slf4j;

import com.ecommerce.orderservice.dto.SoftReserveRequestDTO;
import com.ecommerce.orderservice.dto.SoftReserveResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class InventoryServiceClientImpl implements InventoryServiceClient {

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    private final RestTemplate restTemplate;

    public InventoryServiceClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public SoftReserveResponseDTO softReserve(SoftReserveRequestDTO request) {
        String url = inventoryServiceUrl + "/api/inventory/soft-reserve";
        log.info("Request url: {} - body: {}", url,request);
        // In a real application, you'd handle success/failure based on HTTP status codes
        // and potentially more complex response bodies.
        try {
            return restTemplate.postForObject(url, request, SoftReserveResponseDTO.class);
        } catch (Exception e) {
            // Log the exception and return a failure response
           log.info("Error during soft reservation: " + e.getMessage());
            return new SoftReserveResponseDTO(false, "Failed to reserve inventory: " + e.getMessage());
        }
    }
}