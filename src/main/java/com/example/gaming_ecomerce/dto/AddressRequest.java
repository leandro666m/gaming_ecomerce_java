package com.example.gaming_ecomerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    @NotBlank(message = "El nombre de la dirección es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String city;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 100, message = "El estado no puede exceder 100 caracteres")
    private String state;

    @NotBlank(message = "El código postal es obligatorio")
    @Size(max = 20, message = "El código postal no puede exceder 20 caracteres")
    private String postalCode;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 30, message = "El teléfono no puede exceder 30 caracteres")
    private String phone;
}
