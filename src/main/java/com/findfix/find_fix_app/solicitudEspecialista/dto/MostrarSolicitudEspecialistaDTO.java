package com.findfix.find_fix_app.solicitudEspecialista.dto;

import java.time.LocalDate;

public record MostrarSolicitudEspecialistaDTO(
        LocalDate fechaSolicitud,
        String estado,
        String email,
        String respuesta
        ) {

}
