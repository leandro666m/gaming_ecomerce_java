package com.example.gaming_ecomerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistRequest {

    @NotNull(message = "La lista de juegos no puede ser nula")
    private List<Long> gameIds;
}
