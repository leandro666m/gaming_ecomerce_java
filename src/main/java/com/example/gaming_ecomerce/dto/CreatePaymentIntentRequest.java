package com.example.gaming_ecomerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreatePaymentIntentRequest(
        @NotEmpty List<@Valid CheckoutItemRequest> items) {
}