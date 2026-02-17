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
    String fotoUrlEspecialista;

    public MostrarSolicitudTrabajoDTO(SolicitudTrabajo solicitudTrabajo) {
        this.id = solicitudTrabajo.getSolicitudTrabajoId();
        this.fechaCreacion = solicitudTrabajo.getFechaCreacion();
        this.estado = solicitudTrabajo.getEstado().toString();
        this.nombreEspecialista = solicitudTrabajo.getEspecialista() != null ? solicitudTrabajo.getEspecialista().getUsuario().getNombre() : "Especialista desvinculado";
        this.apellidoEspecialista = solicitudTrabajo.getEspecialista() !=null ? solicitudTrabajo.getEspecialista().getUsuario().getApellido() : "Especialista desvinculado";
        this.descripcion = solicitudTrabajo.getDescripcion();
        this.fotoUrlEspecialista = solicitudTrabajo.getEspecialista().getUsuario().getFotoUrl();
    }
}
