package com.findfix.find_fix_app.trabajo.trabajoExterno.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record CrearTrabajoExternoDTO(
        @NotBlank(message = "El nombre del cliente no puede quedar en blanco.")
        @NotNull(message = "El nombre del cliente es obligatoria.")
        @Size(min = 1, max = 20, message = "El nombre del cliente debe tener entre 1 y 20 caracteres")
        String nombreCliente,

        @NotBlank(message="La descripción no puede quedar en blanco.")
        @NotNull(message = "La descripción es obligatoria.")
        @Size(min = 1, max = 250, message = "La descripción debe tener entre 1 y 250 caracteres")
        String descripcion,

        @NotNull(message = "El presupuesto no puede ser nulo.")
        @Positive(message = "El presupuesto debe ser mayor a cero.")
        Double presupuesto,

        @NotBlank(message="El título no puede quedar en blanco.")
        @NotNull(message = "El título es obligatorio.")
        @Size(min = 1, max = 20, message = "El titulo debe tener entre 1 y 20 caracteres")
        String titulo
){
}
