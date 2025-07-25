package com.ecommerce.inventoryservice.controller;

import lombok.extern.slf4j.Slf4j;

import com.ecommerce.inventoryservice.dto.SoftReserveRequestDTO;
import com.ecommerce.inventoryservice.dto.SoftReserveResponseDTO;
import com.ecommerce.inventoryservice.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/soft-reserve")
    public ResponseEntity<SoftReserveResponseDTO> softReserve(@RequestBody SoftReserveRequestDTO request) {
        SoftReserveResponseDTO response = inventoryService.softReserve(request);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/{productId}")
    public ResponseEntity<SoftReserveResponseDTO> getInventory(@PathVariable Long productId) {
        // SoftReserveResponseDTO is reused here to return current available quantity and a message
        SoftReserveResponseDTO response = inventoryService.getInventory(productId);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // Or other appropriate status
        }
    }
}