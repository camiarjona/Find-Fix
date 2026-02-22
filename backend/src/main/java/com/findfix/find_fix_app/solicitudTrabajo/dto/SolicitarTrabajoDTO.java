package com.findfix.find_fix_app.solicitudTrabajo.dto;

import jakarta.validation.constraints.*;

public record SolicitarTrabajoDTO(
        @Size(min = 1, max = 250)
        @NotNull(message = "Por favor, ingrese una descripción para solicitar el trabajo.")
        @NotBlank(message = "La descripción no puede quedar vacía.")

        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9\\s.,!?¿¡()\\-]+$",
                message = "La descripción contiene caracteres no permitidos"
        )
        String descripcion,

        @Email(message = "Ingrese un formato de email válido.")
        @NotNull(message = "Por favor, ingrese un especialista para solicitarle el trabajo.")
        @NotBlank(message = "El especialista no puede quedar vacío.")
        String emailEspecialista
) { }