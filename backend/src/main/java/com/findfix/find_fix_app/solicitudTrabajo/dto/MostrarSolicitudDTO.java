package com.findfix.find_fix_app.solicitudTrabajo.dto;

import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MostrarSolicitudDTO {
    Long id;
    LocalDate fechaCreacion;
    String estado;
    String nombreCliente;
    String apellidoCliente;
    String descripcion;

    public MostrarSolicitudDTO(SolicitudTrabajo solicitudTrabajo) {
        this.id = solicitudTrabajo.getSolicitudTrabajoId();
        this.fechaCreacion = solicitudTrabajo.getFechaCreacion();
        this.estado = solicitudTrabajo.getEstado().toString();
        this.nombreCliente = solicitudTrabajo.getUsuario() != null ? solicitudTrabajo.getUsuario().getNombre() : "Usuario desvinculado";
        this.apellidoCliente = solicitudTrabajo.getUsuario() != null ? solicitudTrabajo.getUsuario().getApellido() : "Usuario desvinculado";
        this.descripcion = solicitudTrabajo.getDescripcion();
    }
}
