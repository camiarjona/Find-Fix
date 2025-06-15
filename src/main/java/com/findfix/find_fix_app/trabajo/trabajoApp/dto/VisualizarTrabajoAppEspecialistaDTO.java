package com.findfix.find_fix_app.trabajo.trabajoApp.dto;

import com.findfix.find_fix_app.enums.EstadosTrabajos;

import java.time.LocalDate;

public record VisualizarTrabajoAppEspecialistaDTO(
        String nombreCliente,
        String titulo,
        String descripcion,
        EstadosTrabajos estado,
        Double presupuesto,
        LocalDate fechaInicio,
        LocalDate fechaFin

) {
}
