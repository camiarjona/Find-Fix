package com.findfix.find_fix_app.utils.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroDTO(
        @Email(message = "Ingrese un formato de email válido.")
        String email,

        @NotBlank(message = "La contraseña no puede quedar vacía.")
        @Size(min = 6, max = 12, message = "La contraseña debe tener entre 6 y 12 caracteres.")
        String password,

        @NotBlank(message = "El nombre no puede quedar en blanco")
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
                message = "El nombre no debe tener espacios al principio, al final ni múltiples espacios seguidos"
        )
        String nombre,

        @NotBlank(message = "El apellido no puede quedar en blanco")
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
                message = "El apellido no debe tener espacios al principio, al final ni múltiples espacios seguidos"
        )
        String apellido

) {
}
