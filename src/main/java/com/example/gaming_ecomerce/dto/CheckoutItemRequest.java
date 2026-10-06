package com.example.gaming_ecomerce.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CheckoutItemRequest(
        @NotNull Long gameId,
        @NotNull @Min(1) @Max(30) Integer quantity) {
}