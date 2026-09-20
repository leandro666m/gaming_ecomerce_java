package com.example.gaming_ecomerce.dto;

import jakarta.validation.constraints.NotBlank;
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
}
