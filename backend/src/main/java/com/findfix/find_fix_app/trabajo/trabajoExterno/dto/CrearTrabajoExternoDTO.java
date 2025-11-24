package com.findfix.find_fix_app.trabajo.trabajoExterno.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record CrearTrabajoExternoDTO(
        @NotBlank(message = "El nombre del cliente no puede quedar en blanco.")
        @NotNull(message = "El nombre del cliente es obligatoria.")
        @Size(min = 1, max = 20, message = "El nombre del cliente debe tener entre 1 y 20 caracteres")
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
                message = "El nombre del cliente no debe tener espacios al principio, al final ni múltiples espacios seguidos"
        )
        String nombreCliente,

        @NotBlank(message="La descripción no puede quedar en blanco.")
        @NotNull(message = "La descripción es obligatoria.")
        @Size(min = 1, max = 250, message = "La descripción debe tener entre 1 y 250 caracteres")
        String descripcion,

        @NotNull(message = "El presupuesto no puede ser nulo.")
        Double presupuesto,

        @NotBlank(message="El título no puede quedar en blanco.")
        @NotNull(message = "El título es obligatorio.")
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9]+( [A-Za-zÁÉÍÓÚáéíóúÑñ0-9]+)*$",
                message = "El titulo no debe tener espacios al principio, al final ni múltiples espacios seguidos"
        )
        @Size(min = 1, max = 20, message = "El titulo debe tener entre 1 y 20 caracteres")
        String titulo
){
}
