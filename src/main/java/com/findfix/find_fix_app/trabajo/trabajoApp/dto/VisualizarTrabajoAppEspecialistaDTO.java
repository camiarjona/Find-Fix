package com.findfix.find_fix_app.trabajo.trabajoApp.dto;

import com.findfix.find_fix_app.utils.enums.EstadosTrabajos;
import com.findfix.find_fix_app.trabajo.trabajoApp.model.TrabajoApp;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class VisualizarTrabajoAppEspecialistaDTO {
        private String nombreCliente;
    private String titulo;
    private String descripcion;
    private EstadosTrabajos estado;
    private Double presupuesto;
    private String fechaInicio;
    private String fechaFin;

    private static final DateTimeFormatter formatoAmigable = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public VisualizarTrabajoAppEspecialistaDTO(TrabajoApp trabajoApp) {
        this.nombreCliente = trabajoApp.getUsuario().getNombre();
        this.titulo = trabajoApp.getTitulo();
        this.descripcion = trabajoApp.getDescripcion();
        this.estado = trabajoApp.getEstado();
        this.presupuesto = trabajoApp.getPresupuesto();
        this.fechaInicio = (trabajoApp.getFechaInicio() == null) ? "No se inició el trabajo aún" :  trabajoApp.getFechaInicio().format(formatoAmigable);
        this.fechaFin = (trabajoApp.getFechaFin() == null) ? "No se finalizó el trabajo aún" : trabajoApp.getFechaFin().format(formatoAmigable);
    }
}
