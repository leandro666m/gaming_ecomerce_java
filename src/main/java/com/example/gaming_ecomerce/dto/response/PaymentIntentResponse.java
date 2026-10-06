package com.example.gaming_ecomerce.dto.response;

public record PaymentIntentResponse(
        String paymentIntentId,
        String clientSecret,
        Long amount,
        String currency) {
}