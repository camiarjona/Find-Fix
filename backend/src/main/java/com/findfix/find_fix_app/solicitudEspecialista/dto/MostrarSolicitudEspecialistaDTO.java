package com.findfix.find_fix_app.solicitudEspecialista.dto;

import com.findfix.find_fix_app.solicitudEspecialista.model.SolicitudEspecialista;
import lombok.Data;

import lombok.Getter;

import java.time.LocalDate;

@Data
@Getter
public class MostrarSolicitudEspecialistaDTO{
        private long seId;
        private LocalDate fechaSolicitud;
        private String estado;
        private String email;
        private String respuesta;

        public MostrarSolicitudEspecialistaDTO(SolicitudEspecialista  solicitud) {
            this.seId = solicitud.getSeId();
                this.fechaSolicitud = solicitud.getFechaSolicitud();
                this.estado = solicitud.getEstado().getNombreAmigable();
                this.email = solicitud.getUsuario() ==  null ? "Usuario desvinculado" : solicitud.getUsuario().getEmail();
                this.respuesta = solicitud.getRespuesta() == null ? "Sin respuesta" : solicitud.getRespuesta();
        }
}
