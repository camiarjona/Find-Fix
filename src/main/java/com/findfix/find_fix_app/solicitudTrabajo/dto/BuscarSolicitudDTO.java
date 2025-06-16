package com.findfix.find_fix_app.solicitudTrabajo.dto;

import java.time.LocalDate;

public record BuscarSolicitudDTO(
        LocalDate desde,
        LocalDate hasta,
        String emailEspecialista,
        String estado
) {
    public boolean tieneFecha() { return desde != null || hasta != null; }
    public boolean tieneEstado() { return estado != null && !estado.isEmpty(); }
}
