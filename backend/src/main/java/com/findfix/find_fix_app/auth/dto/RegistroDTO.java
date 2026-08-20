package com.findfix.find_fix_app.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroDTO(
        @Email(message = "Ingrese un formato de email válido.")
        @NotBlank(message = "El email es obligatorio.")
        String email,

        @NotBlank(message = "La contraseña no puede quedar vacía.")
        @Size(min = 6, max = 12, message = "La contraseña debe tener entre 6 y 12 caracteres.")
        String password,

        @NotBlank(message = "El nombre no puede quedar en blanco")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
                message = "El nombre solo debe contener letras y un espacio entre palabras."
        )
        String nombre,

        @NotBlank(message = "El apellido no puede quedar en blanco")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres.")
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
                message = "El apellido solo debe contener letras y un espacio entre palabras."
        )
        String apellido,

        @NotBlank(message = "La ciudad es obligatoria")
        String ciudad,

        Double latitud,

        Double longitud

) {
}