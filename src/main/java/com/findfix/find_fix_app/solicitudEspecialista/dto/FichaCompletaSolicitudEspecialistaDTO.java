package com.findfix.find_fix_app.solicitudEspecialista.dto;

import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;

import java.time.LocalDate;

public class FichaCompletaSolicitudEspecialistaDTO {
        private LocalDate fechaSolicitud;
        private LocalDate fechaResolucion;
        private String estado;
        private String motivo;
        private String respuesta;
        private String email;

    public FichaCompletaSolicitudEspecialistaDTO(SolicitudEspecialista solicitudEspecialista) {
        this.fechaSolicitud = solicitudEspecialista.getFechaSolicitud();
        this.fechaResolucion = solicitudEspecialista.getFechaResolucion();
        this.estado = solicitudEspecialista.getEstado().toString();
        this.motivo = solicitudEspecialista.getMotivo();
        this.respuesta = solicitudEspecialista.getRespuesta();
        this.email = solicitudEspecialista.getUsuario().getEmail();
    }
}
