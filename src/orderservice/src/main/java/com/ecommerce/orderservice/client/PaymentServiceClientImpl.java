package com.ecommerce.orderservice.client;

import com.ecommerce.orderservice.dto.PaymentRequestDTO;
import com.ecommerce.orderservice.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class PaymentServiceClientImpl implements PaymentServiceClient {

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    private final RestTemplate restTemplate;

    public PaymentServiceClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        String url = paymentServiceUrl + "/api/payments/process";
        log.info("Sending payment request for orderId: {}", request.getOrderId());
        try {
            return restTemplate.postForObject(url, request, PaymentResponseDTO.class);
        } catch (Exception e) {
            log.error("Error processing payment for orderId: {}", request.getOrderId(), e);
            return new PaymentResponseDTO(false, null, "Payment processing failed: " + e.getMessage());
        }
    }
}