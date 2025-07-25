package com.ecommerce.inventoryservice.service;

import lombok.extern.slf4j.Slf4j;

import com.ecommerce.inventoryservice.dto.SoftReserveRequestDTO;
import com.ecommerce.inventoryservice.dto.SoftReserveResponseDTO;
import com.ecommerce.inventoryservice.model.Inventory;
import com.ecommerce.inventoryservice.model.InventoryLog;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import com.ecommerce.inventoryservice.repository.InventoryLogRepository;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisTemplate<String, Object> redisTemplate;

    public InventoryService(InventoryRepository inventoryRepository, InventoryLogRepository inventoryLogRepository,
                            KafkaTemplate<String, InventoryUpdatedEvent> kafkaTemplate,
                            RedisTemplate<String, Object> redisTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryLogRepository = inventoryLogRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public SoftReserveResponseDTO softReserve(SoftReserveRequestDTO request) {
        log.info("softReserve - start for productId: {} quantity: {}", request.getProductId(), request.getQuantity());
        try {
            String redisKeyAvailable = "inventory:" + request.getProductId() + ":available";
            String redisKeyReserved = "inventory:" + request.getProductId() + ":reserved";

            // Try to soft reserve in Redis first
            Long newAvailableQuantityRedis = redisTemplate.opsForValue().decrement(redisKeyAvailable, request.getQuantity());

            if (newAvailableQuantityRedis == null || newAvailableQuantityRedis < 0) {
                // If Redis operation failed or insufficient quantity in Redis, attempt to re-sync with DB
                log.warn("Soft reservation failed in Redis for product {}. Attempting to re-sync with DB.", request.getProductId());
                Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(request.getProductId());

                if (inventoryOptional.isEmpty()) {
                    log.error("Product not found in inventory (DB) for soft reservation: {}", request.getProductId());
                    return new SoftReserveResponseDTO(false, "Product not found in inventory.", 0);
                }

                Inventory inventory = inventoryOptional.get();
                // Resync Redis with DB values
                redisTemplate.opsForValue().set(redisKeyAvailable, String.valueOf(inventory.getAvailableQuantity()));
                redisTemplate.opsForValue().set(redisKeyReserved, String.valueOf(inventory.getReservedQuantity()));

                // Retry soft reserve in Redis after resync
                newAvailableQuantityRedis = redisTemplate.opsForValue().decrement(redisKeyAvailable, request.getQuantity());
                if (newAvailableQuantityRedis == null || newAvailableQuantityRedis < 0) {
                    log.warn("Not enough stock available after re-sync for product {}. Requested: {}, Available: {}",
                            request.getProductId(), request.getQuantity(), inventory.getAvailableQuantity());
                    return new SoftReserveResponseDTO(false, "Not enough stock available after re-sync.", inventory.getAvailableQuantity());
                }
            }

            // If Redis soft reserve was successful, update reserved quantity in Redis
            redisTemplate.opsForValue().increment(redisKeyReserved, request.getQuantity());

            // Update DB
            Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(request.getProductId());
            Inventory inventory = inventoryOptional.orElseGet(() -> {
                log.warn("Inventory not found in DB for product {}. Creating new entry.", request.getProductId());
                return new Inventory(request.getProductId(), 0, 0);
            });

            inventory.setAvailableQuantity(newAvailableQuantityRedis.intValue());
            inventory.setReservedQuantity(inventory.getReservedQuantity() + request.getQuantity());
            inventory.setLastUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);

            // Log the change
            InventoryLog logEntry = new InventoryLog(
                    request.getProductId(),
                    "SOFT_RESERVE",
                    request.getQuantity(),
                    newAvailableQuantityRedis.intValue(),
                    request.getOrderId()
            );
            inventoryLogRepository.save(logEntry);
            log.info("Soft reservation successful for productId: {} new available quantity: {}", request.getProductId(), newAvailableQuantityRedis.intValue());
            return new SoftReserveResponseDTO(true, "Soft reservation successful.", newAvailableQuantityRedis.intValue());
        } catch (org.springframework.dao.DataAccessException e) {
            // Catches exceptions related to database operations (e.g., InventoryRepository.save)
            // and Redis operations (e.g., redisTemplate.opsForValue().decrement/increment).
            // This provides specific handling for data access issues.
            log.error("Data access error during soft reservation for productId: {}. Error: {}", request.getProductId(), e.getMessage(), e);
            return new SoftReserveResponseDTO(false, "A data access error occurred during soft reservation.", 0);
        } catch (Exception e) {
            // Catches any other unexpected runtime exceptions that might occur.
            // This ensures that all unhandled exceptions are logged and a generic error response is returned.
            log.error("An unexpected error occurred during soft reservation for productId: {}. Error: {}", request.getProductId(), e.getMessage(), e);
            return new SoftReserveResponseDTO(false, "An unexpected error occurred during soft reservation.", 0);
        }
    }

    @Transactional
    public void hardReserveInventory(Long productId, int quantity, Long orderId) {
        log.info("hardReserveInventory - start for productId: {} quantity: {} orderId: {}", productId, quantity, orderId);
        try {
            String redisKeyAvailable = "inventory:" + productId + ":available";
            String redisKeyReserved = "inventory:" + productId + ":reserved";

            // Decrease reserved quantity in Redis
            Long newReservedQuantityRedis = redisTemplate.opsForValue().decrement(redisKeyReserved, quantity);

            if (newReservedQuantityRedis == null || newReservedQuantityRedis < 0) {
                log.error("Hard reservation failed in Redis for product {}: insufficient reserved quantity or Redis error. Reserved: {}. Attempting to re-sync with DB.", productId, newReservedQuantityRedis);
                // Attempt to re-sync with DB and retry if necessary, or handle as a critical error
                Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(productId);
                if (inventoryOptional.isPresent()) {
                    Inventory inventory = inventoryOptional.get();
                    redisTemplate.opsForValue().set(redisKeyReserved, String.valueOf(inventory.getReservedQuantity()));
                    newReservedQuantityRedis = redisTemplate.opsForValue().decrement(redisKeyReserved, quantity);
                    if (newReservedQuantityRedis == null || newReservedQuantityRedis < 0) {
                        log.error("Hard reservation failed even after Redis re-sync for product {}. Insufficient reserved quantity.", productId);
                        return; // Cannot proceed with hard reservation
                    }
                } else {
                    log.error("Product not found in inventory (DB) for hard reservation during Redis re-sync attempt: {}", productId);
                    return; // Cannot proceed with hard reservation
                }
            }

            Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(productId);
            if (inventoryOptional.isEmpty()) {
                log.error("Product not found in inventory (DB) for hard reservation: {}. This indicates a critical inconsistency.", productId);
                // Critical inconsistency, Redis was updated but DB is missing product
                return;
            }

            Inventory inventory = inventoryOptional.get();

            // Update DB
            int newReservedQuantity = inventory.getReservedQuantity() - quantity;
            if (newReservedQuantity < 0) {
                log.error("Attempted to set negative reserved quantity for product {} in DB. Current reserved: {}, Quantity to deduct: {}",
                        productId, inventory.getReservedQuantity(), quantity);
                return; // Prevent negative reserved quantity in DB
            }

            inventory.setReservedQuantity(newReservedQuantity);
            inventory.setLastUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);

            // Log the change
            InventoryLog logEntry = new InventoryLog(
                    productId,
                    "HARD_RESERVE",
                    quantity,
                    inventory.getAvailableQuantity(), // Log current available quantity from DB
                    orderId
            );
            inventoryLogRepository.save(logEntry);

            // Publish inventory.updated event
            kafkaTemplate.send("inventory.updated", new InventoryUpdatedEvent(productId, inventory.getAvailableQuantity()));
            log.info("hardReserveInventory successful for productId: {}. Published InventoryUpdatedEvent with new quantity: {}", productId, inventory.getAvailableQuantity());
        } catch (org.springframework.dao.DataAccessException e) {
            // Catches exceptions related to database operations (e.g., InventoryRepository.save)
            // and Redis operations (e.g., redisTemplate.opsForValue().decrement/increment).
            // This provides specific handling for data access issues.
            log.error("Data access error during hard reservation for productId: {}. Error: {}", productId, e.getMessage(), e);
        } catch (org.springframework.kafka.KafkaException e) {
            // Catches exceptions related to Kafka operations (e.g., kafkaTemplate.send).
            // This provides specific handling for messaging issues.
            log.error("Kafka error during hard reservation for productId: {}. Error: {}", productId, e.getMessage(), e);
        } catch (Exception e) {
            // Catches any other unexpected runtime exceptions that might occur.
            log.error("An unexpected error occurred during hard reservation for productId: {}. Error: {}", productId, e.getMessage(), e);
        }
    }

    @Transactional
    public void rollbackSoftReservation(Long productId, int quantity, Long orderId) {
        log.info("rollbackSoftReservation - start for productId: {} quantity: {} orderId: {}", productId, quantity, orderId);
        try {
            String redisKeyAvailable = "inventory:" + productId + ":available";
            String redisKeyReserved = "inventory:" + productId + ":reserved";

            // Increase available quantity and decrease reserved quantity in Redis
            Long newAvailableQuantityRedis = redisTemplate.opsForValue().increment(redisKeyAvailable, quantity);
            Long newReservedQuantityRedis = redisTemplate.opsForValue().decrement(redisKeyReserved, quantity);

            if (newAvailableQuantityRedis == null || newReservedQuantityRedis == null || newReservedQuantityRedis < 0) {
                log.error("Rollback soft reservation failed in Redis for product {}: Redis error or inconsistent state. Available: {}, Reserved: {}",
                        productId, newAvailableQuantityRedis, newReservedQuantityRedis);
                // Attempt to re-sync with DB and retry if necessary, or handle as a critical error
                Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(productId);
                if (inventoryOptional.isPresent()) {
                    Inventory inventory = inventoryOptional.get();
                    redisTemplate.opsForValue().set(redisKeyAvailable, String.valueOf(inventory.getAvailableQuantity()));
                    redisTemplate.opsForValue().set(redisKeyReserved, String.valueOf(inventory.getReservedQuantity()));
                    newAvailableQuantityRedis = redisTemplate.opsForValue().increment(redisKeyAvailable, quantity);
                    newReservedQuantityRedis = redisTemplate.opsForValue().decrement(redisKeyReserved, quantity);
                    if (newAvailableQuantityRedis == null || newReservedQuantityRedis == null || newReservedQuantityRedis < 0) {
                        log.error("Rollback soft reservation failed even after Redis re-sync for product {}. Inconsistent state.", productId);
                        return; // Cannot proceed with rollback
                    }
                } else {
                    log.error("Product not found in inventory (DB) for rollback during Redis re-sync attempt: {}", productId);
                    return; // Cannot proceed with rollback
                }
            }

            Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(productId);
            if (inventoryOptional.isEmpty()) {
                log.warn("Product not found in inventory (DB) for soft reservation rollback: {}", productId);
                return;
            }

            Inventory inventory = inventoryOptional.get();

            // Update DB
            int newAvailableQuantity = inventory.getAvailableQuantity() + quantity;
            int newReservedQuantity = inventory.getReservedQuantity() - quantity;
            if (newReservedQuantity < 0) {
                log.error("Attempted to set negative reserved quantity for product {} during rollback. Current reserved: {}, Quantity to deduct: {}",
                        productId, inventory.getReservedQuantity(), quantity);
                newReservedQuantity = 0; // Prevent negative reserved quantity in DB
            }

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
            log.info("rollbackSoftReservation successful for product: {}. Published InventoryUpdatedEvent with new quantity: {}", productId, newAvailableQuantity);
        } catch (org.springframework.dao.DataAccessException e) {
            // Catches exceptions related to database operations (e.g., InventoryRepository.save)
            // and Redis operations (e.g., redisTemplate.opsForValue().increment/decrement).
            // This provides specific handling for data access issues.
            log.error("Data access error during rollback soft reservation for productId: {}. Error: {}", productId, e.getMessage(), e);
        } catch (org.springframework.kafka.KafkaException e) {
            // Catches exceptions related to Kafka operations (e.g., kafkaTemplate.send).
            // This provides specific handling for messaging issues.
            log.error("Kafka error during rollback soft reservation for productId: {}. Error: {}", productId, e.getMessage(), e);
        } catch (Exception e) {
            // Catches any other unexpected runtime exceptions that might occur.
            log.error("An unexpected error occurred during rollback soft reservation for productId: {}. Error: {}", productId, e.getMessage(), e);
        }
    }

    public SoftReserveResponseDTO getInventory(Long productId) {
        log.info("getInventory - start for productId: {}", productId);
        try {
            String redisKeyAvailable = "inventory:" + productId + ":available";
            String redisKeyReserved = "inventory:" + productId + ":reserved";

            Object availableQuantityObj = redisTemplate.opsForValue().get(redisKeyAvailable);
            Object reservedQuantityObj = redisTemplate.opsForValue().get(redisKeyReserved);

            if (availableQuantityObj != null && reservedQuantityObj != null) {
                try {
                    int available = Integer.parseInt(availableQuantityObj.toString());
                    int reserved = Integer.parseInt(reservedQuantityObj.toString());
                    log.info("Inventory for product {} found in Redis. Available: {}, Reserved: {}", productId, available, reserved);
                    return new SoftReserveResponseDTO(true, "Inventory found in cache.", available);
                } catch (NumberFormatException e) {
                    log.error("Error parsing Redis inventory quantity for product {}: {}. Falling back to DB.", productId, e.getMessage(), e);
                    // Fallback to DB if Redis data is corrupted
                }
            }

            // If not in Redis or Redis data corrupted, fetch from DB and populate Redis
            log.info("Inventory for product {} not found in Redis or corrupted. Fetching from DB.", productId);
            Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(productId);

            if (inventoryOptional.isPresent()) {
                Inventory inventory = inventoryOptional.get();
                // Ensure Redis quantities are stored as integers to support INCR/DECR operations
                redisTemplate.opsForValue().set(redisKeyAvailable, inventory.getAvailableQuantity());
                redisTemplate.opsForValue().set(redisKeyReserved, inventory.getReservedQuantity());
                log.info("Inventory for product {} fetched from DB and cached. Available: {}, Reserved: {}",
                        productId, inventory.getAvailableQuantity(), inventory.getReservedQuantity());
                return new SoftReserveResponseDTO(true, "Inventory fetched from DB.", inventory.getAvailableQuantity());
            } else {
                log.warn("Product not found in inventory (DB) for getInventory: {}", productId);
                return new SoftReserveResponseDTO(false, "Product not found in inventory.", 0);
            }
        } catch (NumberFormatException e) {
            // Catches errors when parsing Redis quantities from String to Integer.
            // This indicates corrupted data in Redis, and the fallback to DB handles this.
            log.error("Error parsing Redis inventory quantity for product {}: {}. Falling back to DB.", productId, e.getMessage(), e);
            return new SoftReserveResponseDTO(false, "Corrupted inventory data in cache. Please try again.", 0);
        } catch (org.springframework.dao.DataAccessException e) {
            // Catches exceptions related to database operations (e.g., InventoryRepository.findByProductId).
            // This provides specific handling for data access issues.
            log.error("Data access error during getInventory for productId: {}. Error: {}", productId, e.getMessage(), e);
            return new SoftReserveResponseDTO(false, "A data access error occurred while fetching inventory.", 0);
        } catch (Exception e) {
            // Catches any other unexpected runtime exceptions that might occur.
            log.error("An unexpected error occurred during getInventory for productId: {}. Error: {}", productId, e.getMessage(), e);
            return new SoftReserveResponseDTO(false, "An unexpected error occurred while fetching inventory.", 0);
        }
    }
}