package com.findfix.find_fix_app.solicitudEspecialista.dto;

import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Data
@Getter
public class FichaCompletaSolicitudEspecialistaDTO {
        private long seId;
        private LocalDate fechaSolicitud;
        private LocalDate fechaResolucion;
        private String estado;
        private String motivo;
        private String respuesta;
        private String email;

    private static final DateTimeFormatter formatoAmigable = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FichaCompletaSolicitudEspecialistaDTO(SolicitudEspecialista solicitudEspecialista) {
        this.seId = solicitudEspecialista.getSeId();
        this.fechaSolicitud = solicitudEspecialista.getFechaSolicitud();
        this.fechaResolucion = solicitudEspecialista.getFechaResolucion();
        this.estado = solicitudEspecialista.getEstado().toString();
        this.motivo = solicitudEspecialista.getMotivo();
        this.respuesta = (solicitudEspecialista.getRespuesta() == null) ? "No se ha respondido a la solicitud" : solicitudEspecialista.getRespuesta();
        this.email = solicitudEspecialista.getUsuario() != null ? solicitudEspecialista.getUsuario().getEmail() : "Usuario desvinculado";
    }

}
