package com.findfix.find_fix_app.trabajo.trabajoExterno.dto;

import com.findfix.find_fix_app.enums.EstadosTrabajos;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrabajoExternoDTO {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombreCliente;

    @NotNull(message = "La fecha de inicio no puede ser nula")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin no puede ser nula")
    private LocalDate fechaFin;

    @NotNull(message = "El estado del trabajo es obligatorio")
    private EstadosTrabajos estado;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 5, max = 500, message = "La descripción debe tener entre 5 y 500 caracteres")
    private String descripcion;

    @NotNull(message = "El presupuesto no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El presupuesto debe ser mayor a cero")
    private Double presupuesto;

    @NotNull(message = "El ID del especialista es obligatorio")
    private Long especialistaId;


    public TrabajoExternoDTO(TrabajoExterno trabajo) {
        this.nombreCliente = trabajo.getNombreCliente();
        this.fechaInicio = trabajo.getFechaInicio();
        this.fechaFin = trabajo.getFechaFin();
        this.estado = trabajo.getEstado();
        this.descripcion = trabajo.getDescripcion();
        this.presupuesto = trabajo.getPresupuesto();
        this.especialistaId = trabajo.getEspecialista() != null ? trabajo.getEspecialista().getEspecialistaId() : null;
    }
}