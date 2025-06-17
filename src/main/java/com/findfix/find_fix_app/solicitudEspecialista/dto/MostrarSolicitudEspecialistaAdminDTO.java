package com.findfix.find_fix_app.solicitudEspecialista.dto;

import java.time.LocalDate;

public record MostrarSolicitudEspecialistaAdminDTO(
        Long id,
        LocalDate fechaSolicitud,
        String estado,
        String email
) {
}
