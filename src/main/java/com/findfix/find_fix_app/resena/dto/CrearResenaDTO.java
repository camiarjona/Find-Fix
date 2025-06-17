package com.findfix.find_fix_app.resena.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearResenaDTO {

    @NotNull(message = "La puntuación no puede ser nula")
    @DecimalMin(value = "1.0", message = "La puntuación mínima es 1")
    @DecimalMax(value = "5.0", message = "La puntuación máxima es 5")
    private Double puntuacion;

    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(min = 5, max = 500, message = "El comentario debe tener entre 5 y 500 caracteres")
    private String comentario;

    @NotNull(message = "Debe indicarse el ID del trabajo asociado")
    private Long trabajoId;

}