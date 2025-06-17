package com.findfix.find_fix_app.solicitudEspecialista.dto;

import java.time.LocalDate;

public record MostrarSolicitudEspecialistaAdminDTO(
        LocalDate fechaSolicitud,
        String estado,
        String email
) {
}
