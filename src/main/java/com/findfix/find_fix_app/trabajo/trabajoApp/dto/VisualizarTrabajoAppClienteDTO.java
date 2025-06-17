package com.findfix.find_fix_app.trabajo.trabajoApp.dto;

import com.findfix.find_fix_app.utils.enums.EstadosTrabajos;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class VisualizarTrabajoAppClienteDTO {
    private Long id;
    private String nombreEspecialista;
    private String descripcion;
    private EstadosTrabajos estado;
    private Double presupuesto;
    private String fechaInicio;
    private String fechaFin;

    private static final DateTimeFormatter formatoAmigable = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public VisualizarTrabajoAppClienteDTO(TrabajoApp  trabajoApp) {
        this.id = trabajoApp.getTrabajoAppId();
        this.nombreEspecialista = trabajoApp.getEspecialista().getUsuario().getNombre();
        this.descripcion = trabajoApp.getDescripcion();
        this.estado = trabajoApp.getEstado();
        this.presupuesto = trabajoApp.getPresupuesto();
        this.fechaInicio = (trabajoApp.getFechaInicio() == null) ? "No se inició el trabajo aún" :  trabajoApp.getFechaInicio().format(formatoAmigable);
        this.fechaFin = (trabajoApp.getFechaFin() == null) ? "No se finalizó el trabajo aún" : trabajoApp.getFechaFin().format(formatoAmigable);
    }


}
