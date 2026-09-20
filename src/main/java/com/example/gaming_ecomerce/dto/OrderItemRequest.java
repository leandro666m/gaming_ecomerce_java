package com.example.gaming_ecomerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer quantity;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.00", inclusive = true, message = "El precio unitario no puede ser negativo")
    private BigDecimal unitPrice;

    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.00", inclusive = true, message = "El subtotal no puede ser negativo")
    private BigDecimal subtotal;

    @Min(value = 0, message = "El descuento no puede ser negativo")
    private Integer discount;
}
