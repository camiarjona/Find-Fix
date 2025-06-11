package com.findfix.find_fix_app.trabajo.trabajoExterno.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CrearTrabajoExternoDTO {
    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombreCliente;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 5, max = 500, message = "La descripción debe tener entre 5 y 500 caracteres")
    private String descripcion;

    @NotNull(message = "El presupuesto no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El presupuesto debe ser mayor a cero")
    private Double presupuesto;

    @NotNull(message = "El ID del especialista es obligatorio")
    private Long especialistaId;
}
