package com.example.gaming_ecomerce.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformRequest {

    @NotBlank(message = "El nombre de la plataforma es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @NotBlank(message = "El slug es obligatorio")
    @Size(max = 100, message = "El slug no puede exceder 100 caracteres")
    private String slug;

    @JsonAlias("order")
    @NotNull(message = "El orden es obligatorio")
    @Min(value = 0, message = "El orden no puede ser negativo")
    private Integer display_order;

    @Size(max = 1000, message = "La URL del ícono no puede exceder 1000 caracteres")
    private String iconUrl;
}
