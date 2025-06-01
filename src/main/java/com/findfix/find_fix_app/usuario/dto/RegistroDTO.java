package com.findfix.find_fix_app.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroDTO(
        @Email(message = "Ingrese un formato de email válido.")
        String email,

        @NotBlank(message = "La contraseña no puede quedar vacía.")
        @Size(min = 6, max = 12, message = "La contraseña debe tener entre 6 y 12 caracteres.")
        String password,

        @NotBlank(message = "El nombre no puede quedar en blanco")
        String nombre,

        @NotBlank(message = "El nombre no puede quedar en blanco")
        String apellido

) {
}
