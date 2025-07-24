package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.dto.OrderCreationRequestDTO;
import com.ecommerce.orderservice.dto.OrderCreationResponseDTO;
import com.ecommerce.orderservice.dto.OrderDetailsDTO;
import com.ecommerce.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderCreationResponseDTO> createOrder(@RequestBody OrderCreationRequestDTO request) {
        OrderCreationResponseDTO response = orderService.createOrder(request);
        if ("SUCCESS".equals(response.getStatus())) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            // In a real system, more specific error handling and status codes would be used
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsDTO> getOrderById(@PathVariable Long id) {
        try {
            OrderDetailsDTO orderDetails = orderService.getOrderById(id);
            return ResponseEntity.ok(orderDetails);
        } catch (RuntimeException e) { // Catch custom exception for Order Not Found
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderDetailsDTO> cancelOrder(@PathVariable Long id) {
        try {
            OrderDetailsDTO cancelledOrder = orderService.cancelOrder(id);
            return ResponseEntity.ok(cancelledOrder);
        } catch (RuntimeException e) { // Catch custom exception for inability to cancel or not found
            return ResponseEntity.badRequest().build(); // Or other appropriate status
        }
    }
}