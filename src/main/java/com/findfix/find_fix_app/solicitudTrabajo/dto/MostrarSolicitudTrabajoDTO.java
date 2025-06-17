package com.findfix.find_fix_app.solicitudTrabajo.dto;

import com.findfix.find_fix_app.solicitudTrabajo.model.SolicitudTrabajo;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MostrarSolicitudTrabajoDTO {
    Long id;
    LocalDate fechaCreacion;
    String estado;
    String nombreEspecialista;
    String apellidoEspecialista;
    String descripcion;

    public MostrarSolicitudTrabajoDTO(SolicitudTrabajo solicitudTrabajo) {
        this.id = solicitudTrabajo.getSolicitudTrabajoId();
        this.fechaCreacion = solicitudTrabajo.getFechaCreacion();
        this.estado = solicitudTrabajo.getEstado().toString();
        this.nombreEspecialista = solicitudTrabajo.getEspecialista().getUsuario().getNombre();
        this.apellidoEspecialista = solicitudTrabajo.getEspecialista().getUsuario().getApellido();
        this.descripcion = solicitudTrabajo.getDescripcion();
    }
}
