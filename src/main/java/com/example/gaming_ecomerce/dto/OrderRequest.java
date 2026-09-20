package com.example.gaming_ecomerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "El total del pedido es obligatorio")
    @DecimalMin(value = "0.00", inclusive = true, message = "El total no puede ser negativo")
    private BigDecimal totalPayment;

    @Size(max = 100, message = "El ID de pago no puede exceder 100 caracteres")
    private String paymentId;

    @Size(max = 100, message = "El nombre del destinatario no puede exceder 100 caracteres")
    private String shippingName;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String shippingAddress;

    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String shippingCity;

    @Size(max = 100, message = "El estado no puede exceder 100 caracteres")
    private String shippingState;

    @Size(max = 20, message = "El código postal no puede exceder 20 caracteres")
    private String shippingPostalCode;

    @Size(max = 30, message = "El teléfono no puede exceder 30 caracteres")
    private String shippingPhone;
}
