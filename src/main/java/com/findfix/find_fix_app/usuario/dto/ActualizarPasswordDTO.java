package com.findfix.find_fix_app.usuario.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActualizarPasswordDTO(
        @NotNull(message = "No se puede dejar este campo vacío.")
        String passwordActual,
        @NotNull(message = "Por favor, ingrese la nueva contraseña.")
        @Size(min = 6, max = 12, message = "La contraseña debe tener entre 6 y 12 caracteres.")
        String passwordNuevo
){
}
