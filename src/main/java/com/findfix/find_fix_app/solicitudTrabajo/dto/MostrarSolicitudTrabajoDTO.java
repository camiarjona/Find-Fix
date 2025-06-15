package com.findfix.find_fix_app.solicitudTrabajo.dto;

import java.time.LocalDate;

public record MostrarSolicitudTrabajoDTO(
        Long id,
        LocalDate fechaCreacion,
        String estado,
        String nombreEspecialista,
        String apellidoEspecialista
){

}
