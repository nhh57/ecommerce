package com.ecommerce.inventoryservice.service;

import lombok.extern.slf4j.Slf4j;

import com.ecommerce.inventoryservice.dto.SoftReserveRequestDTO;
import com.ecommerce.inventoryservice.dto.SoftReserveResponseDTO;
import com.ecommerce.inventoryservice.model.Inventory;
import com.ecommerce.inventoryservice.model.InventoryLog;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import com.ecommerce.inventoryservice.repository.InventoryLogRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import com.ecommerce.inventoryservice.event.InventoryUpdatedEvent;

@Service
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final KafkaTemplate<String, InventoryUpdatedEvent> kafkaTemplate;

    public InventoryService(InventoryRepository inventoryRepository, InventoryLogRepository inventoryLogRepository,
                            KafkaTemplate<String, InventoryUpdatedEvent> kafkaTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryLogRepository = inventoryLogRepository;
        this.kafkaTemplate = kafkaTemplate;
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

    @Transactional
    public void hardReserveInventory(Long productId, int quantity, Long orderId) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(productId);

        if (inventoryOptional.isEmpty()) {
            // Log error or throw exception: Product not found in inventory for hard reservation
           log.info("Product not found in inventory for hard reservation: " + productId);
            return;
        }

        Inventory inventory = inventoryOptional.get();

        if (inventory.getReservedQuantity() < quantity) {
            // Log error or throw exception: Not enough reserved quantity for hard reservation
           log.info("Not enough reserved quantity for hard reservation for product: " + productId);
            return;
        }

        int newAvailableQuantity = inventory.getAvailableQuantity() - quantity;
        int newReservedQuantity = inventory.getReservedQuantity() - quantity;

        inventory.setAvailableQuantity(newAvailableQuantity);
        inventory.setReservedQuantity(newReservedQuantity);
        inventory.setLastUpdatedAt(LocalDateTime.now());

        inventoryRepository.save(inventory);

        // Log the change
        InventoryLog log = new InventoryLog(
                productId,
                "HARD_RESERVE",
                quantity,
                newAvailableQuantity,
                orderId
        );
        inventoryLogRepository.save(log);

        // Publish inventory.updated event
        kafkaTemplate.send("inventory.updated", new InventoryUpdatedEvent(productId, newAvailableQuantity));
        System.out.println("Published InventoryUpdatedEvent for product: " + productId + " new quantity: " + newAvailableQuantity);
    }
    @Transactional
    public void rollbackSoftReservation(Long productId, int quantity, Long orderId) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(productId);

        if (inventoryOptional.isEmpty()) {
            log.warn("Product not found in inventory for soft reservation rollback: {}", productId);
            return;
        }

        Inventory inventory = inventoryOptional.get();

        if (inventory.getReservedQuantity() < quantity) {
            log.warn("Reserved quantity for product {} is less than rollback quantity {}. Actual reserved: {}", productId, quantity, inventory.getReservedQuantity());
            // This might indicate an inconsistency, decide how to handle (e.g., partial rollback or error)
            // For now, we'll just log and return.
            return;
        }

        int newAvailableQuantity = inventory.getAvailableQuantity() + quantity;
        int newReservedQuantity = inventory.getReservedQuantity() - quantity;

        inventory.setAvailableQuantity(newAvailableQuantity);
        inventory.setReservedQuantity(newReservedQuantity);
        inventory.setLastUpdatedAt(LocalDateTime.now());

        inventoryRepository.save(inventory);

        // Log the change
        InventoryLog logEntry = new InventoryLog(
                productId,
                "ROLLBACK_SOFT_RESERVE",
                quantity,
                newAvailableQuantity,
                orderId
        );
        inventoryLogRepository.save(logEntry);

        // Publish inventory.updated event
        kafkaTemplate.send("inventory.updated", new InventoryUpdatedEvent(productId, newAvailableQuantity));
        log.info("Published InventoryUpdatedEvent for product: {} new quantity: {}", productId, newAvailableQuantity);
    }
}