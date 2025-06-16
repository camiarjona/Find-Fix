package com.findfix.find_fix_app.solicitudEspecialista.dto;

import com.findfix.find_fix_app.enums.EstadosSolicitudes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ActualizarSolicitudEspecialistaDTO (
        String estado,

        @NotNull (message = "La devolucion es obligatoria.")
        @NotBlank (message = "La devolucion no puede quedar en blanco")
        String respuesta
){
}
