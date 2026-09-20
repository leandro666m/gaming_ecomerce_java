package com.example.gaming_ecomerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String title;

    @NotBlank(message = "El slug es obligatorio")
    @Size(max = 200, message = "El slug no puede exceder 200 caracteres")
    private String slug;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.00", inclusive = true, message = "El precio no puede ser negativo")
    private BigDecimal price;

    @Size(max = 5000, message = "El resumen no puede exceder 5000 caracteres")
    private String summary;

    @Size(max = 1000, message = "La URL del video no puede exceder 1000 caracteres")
    private String video;

    @Size(max = 1000, message = "La URL de la portada no puede exceder 1000 caracteres")
    private String cover;

    @Size(max = 1000, message = "La URL del fondo no puede exceder 1000 caracteres")
    private String wallpaper;

    private List<String> screenshots;

    private Integer discount;

    private LocalDate releaseDate;

    @NotNull(message = "Debe indicar la plataforma del juego")
    private Long platformId;
}
