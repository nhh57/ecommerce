package com.ecommerce.orderservice.client;

import com.ecommerce.orderservice.dto.PaymentRequestDTO;
import com.ecommerce.orderservice.dto.PaymentResponseDTO;
import org.springframework.stereotype.Component;

@Component
public interface PaymentServiceClient {
    PaymentResponseDTO processPayment(PaymentRequestDTO request);
}