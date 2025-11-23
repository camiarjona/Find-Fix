package com.findfix.find_fix_app.trabajo.trabajoExterno.dto;

import com.findfix.find_fix_app.trabajo.trabajoExterno.model.TrabajoExterno;
import lombok.Data;

@Data
public class TrabajoExternoDTO {
    private long id;
    private String nombreCliente;
    private String titulo;
    private String fechaInicio;
    private String fechaFin;
    private String estado;
    private String descripcion;
    private Double presupuesto;

    public TrabajoExternoDTO(TrabajoExterno trabajo) {
        this.id = trabajo.getTrabajoExternoId();
        this.titulo = trabajo.getTitulo();
        this.nombreCliente = trabajo.getNombreCliente();
        this.fechaInicio = trabajo.getFechaInicio() == null ? "Sin iniciar" : trabajo.getFechaInicio().toString();
        this.fechaFin = trabajo.getFechaFin() ==  null ? "Sin finalizar" : trabajo.getFechaFin().toString();
        this.estado = trabajo.getEstado().getEstadoAmigable();
        this.descripcion = trabajo.getDescripcion();
        this.presupuesto = trabajo.getPresupuesto();
    }
}