package com.findfix.find_fix_app.solicitudTrabajo.dto;

import java.time.LocalDate;

public record BuscarSolicitudDTO(
        LocalDate fecha,
        String emailEspecialista,
        String estado
) {
}
