package com.example.gaming_ecomerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CompleteCheckoutRequest(
        @NotBlank String paymentIntentId,
        @NotNull Long addressId,
        @NotEmpty List<@Valid CheckoutItemRequest> items) {
}