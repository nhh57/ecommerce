package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.dto.SoftReserveRequestDTO;
import com.ecommerce.inventoryservice.dto.SoftReserveResponseDTO;
import com.ecommerce.inventoryservice.model.Inventory;
import com.ecommerce.inventoryservice.model.InventoryLog;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import com.ecommerce.inventoryservice.repository.InventoryLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;

    public InventoryService(InventoryRepository inventoryRepository, InventoryLogRepository inventoryLogRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryLogRepository = inventoryLogRepository;
    }

    @Transactional
    public SoftReserveResponseDTO softReserve(SoftReserveRequestDTO request) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(request.getProductId());

        if (inventoryOptional.isEmpty()) {
            return new SoftReserveResponseDTO(false, "Product not found in inventory.", 0);
        }

        Inventory inventory = inventoryOptional.get();

        if (inventory.getAvailableQuantity() < request.getQuantity()) {
            return new SoftReserveResponseDTO(false, "Not enough stock available.", inventory.getAvailableQuantity());
        }

        int newAvailableQuantity = inventory.getAvailableQuantity() - request.getQuantity();
        int newReservedQuantity = inventory.getReservedQuantity() + request.getQuantity();

        inventory.setAvailableQuantity(newAvailableQuantity);
        inventory.setReservedQuantity(newReservedQuantity);
        inventory.setLastUpdatedAt(LocalDateTime.now()); // Update timestamp

        inventoryRepository.save(inventory);

        // Log the change
        InventoryLog log = new InventoryLog(
                request.getProductId(),
                "SOFT_RESERVE",
                request.getQuantity(),
                newAvailableQuantity,
                request.getOrderId()
        );
        inventoryLogRepository.save(log);

        return new SoftReserveResponseDTO(true, "Soft reservation successful.", newAvailableQuantity);
    }
}