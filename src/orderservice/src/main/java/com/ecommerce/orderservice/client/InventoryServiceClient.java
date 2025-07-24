package com.ecommerce.orderservice.client;

import com.ecommerce.orderservice.dto.SoftReserveRequestDTO;
import com.ecommerce.orderservice.dto.SoftReserveResponseDTO;
import org.springframework.stereotype.Component;

// This is an interface for a client to the Inventory Service.
// Actual implementation (e.g., using RestTemplate, WebClient, or FeignClient)
// will be provided in a separate class or configuration.
@Component // Mark as a Spring component for autowiring
public interface InventoryServiceClient {

    // Placeholder for soft reservation API call
    SoftReserveResponseDTO softReserve(SoftReserveRequestDTO request);

    // Placeholder for other potential Inventory Service calls
    // void hardReserve(HardReserveRequestDTO request);
    // void rollbackSoftReservation(RollbackRequestDTO request);
}