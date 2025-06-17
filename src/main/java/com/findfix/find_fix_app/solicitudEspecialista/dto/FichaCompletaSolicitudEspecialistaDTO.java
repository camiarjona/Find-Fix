package com.findfix.find_fix_app.solicitudEspecialista.dto;

import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Data
public class FichaCompletaSolicitudEspecialistaDTO {
        private LocalDate fechaSolicitud;
        private String fechaResolucion;
        private String estado;
        private String motivo;
        private String respuesta;
        private String email;

    private static final DateTimeFormatter formatoAmigable = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FichaCompletaSolicitudEspecialistaDTO(SolicitudEspecialista solicitudEspecialista) {
        this.fechaSolicitud = solicitudEspecialista.getFechaSolicitud();
        this.fechaResolucion = String.format((solicitudEspecialista.getFechaResolucion() == null) ? "No se ha respondido a la solicitud" : solicitudEspecialista.getFechaResolucion().format(formatoAmigable));
        this.estado = solicitudEspecialista.getEstado().toString();
        this.motivo = solicitudEspecialista.getMotivo();
        this.respuesta = (solicitudEspecialista.getRespuesta() == null) ? "No se ha respondido a la solicitud" : solicitudEspecialista.getRespuesta();
        this.email = solicitudEspecialista.getUsuario().getEmail();
    }

}
