package com.findfix.find_fix_app.trabajo.trabajoApp.dto;

import com.findfix.find_fix_app.enums.EstadosTrabajos;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public record VisualizarTrabajoAppClienteDTO(
        Long id,
        String nombreEspecialista,
        String descripcion,
        EstadosTrabajos estado,
        Double presupuesto,
        LocalDate fechaInicio,
        LocalDate fechaFin

) {



}
