package com.findfix.find_fix_app.solicitudTrabajo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SolicitarTrabajoDTO(
        @Size(min = 1, max = 250)
        @NotNull(message = "Por favor, ingrese una descripción para solicitar el trabajo.")
        @NotBlank(message = "La descripción no puede quedar vacía.")
        String descripcion,
        @Email(message = "Ingrese un formato de email válido.")
        @NotNull(message = "Por favor, ingrese un especialista para solicitarle el trabajo.")
        @NotBlank(message = "El especialista no puede quedar vacío.")
        String emailEspecialista
) {
}
