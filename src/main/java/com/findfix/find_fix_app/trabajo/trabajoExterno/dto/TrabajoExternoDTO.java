package com.findfix.find_fix_app.trabajo.trabajoExterno.dto;

import com.findfix.find_fix_app.utils.enums.EstadosTrabajos;
import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TrabajoExternoDTO {
    private String nombreCliente;
    private String titulo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadosTrabajos estado;
    private String descripcion;
    private Double presupuesto;

    public TrabajoExternoDTO(TrabajoExterno trabajo) {
        this.titulo = trabajo.getTitulo();
        this.nombreCliente = trabajo.getNombreCliente();
        this.fechaInicio = trabajo.getFechaInicio();
        this.fechaFin = trabajo.getFechaFin();
        this.estado = trabajo.getEstado();
        this.descripcion = trabajo.getDescripcion();
        this.presupuesto = trabajo.getPresupuesto();
    }
}