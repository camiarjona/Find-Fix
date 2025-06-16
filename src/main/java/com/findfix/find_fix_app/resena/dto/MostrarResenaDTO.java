package com.findfix.find_fix_app.resena.dto;

import com.findfix.find_fix_app.enums.EstadosTrabajos;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MostrarResenaDTO {

    @NotNull(message = "La puntuación no puede ser nula")
    @DecimalMin(value = "1.0", message = "La puntuación mínima es 1")
    @DecimalMax(value = "5.0", message = "La puntuación máxima es 5")
    private Double puntuacion;

    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(min = 5, max = 500, message = "El comentario debe tener entre 5 y 500 caracteres")
    private String comentario;

    @NotNull(message = "La fecha de inicio no puede ser nula")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin no puede ser nula")
    private LocalDate fechaFin;

    @NotNull(message = "El estado del trabajo es obligatorio")
    private EstadosTrabajos estado;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 5, max = 500, message = "La descripción debe tener entre 5 y 500 caracteres")
    private String descripcion;

    @NotNull(message = "El presupuesto no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El presupuesto debe ser mayor a cero")
    private Double presupuesto;
}