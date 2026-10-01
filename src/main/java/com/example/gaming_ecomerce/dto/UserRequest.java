package com.example.gaming_ecomerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor
public class UserRequest {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ingresar un email válido")
    private String email;

    @Pattern(regexp = "^$|^.{6,100}$", message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String password;

    @NotBlank (message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String firstName;
    
    @NotBlank (message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    private String lastName;

    @NotBlank (message = "El rol es obligatorio")
    @Pattern(regexp = "user|admin", message = "El rol debe ser user o admin")
    private String role;

    @NotNull(message = "El estado es obligatorio")
    private Boolean active;

}
