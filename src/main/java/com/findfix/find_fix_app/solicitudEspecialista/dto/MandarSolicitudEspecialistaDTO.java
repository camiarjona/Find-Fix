package com.findfix.find_fix_app.solicitudEspecialista.dto;

import jakarta.validation.constraints.Pattern;

public record MandarSolicitudEspecialistaDTO(

        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
                message = "No puede haber espacios al principio ni al final, ni multiples espacios seguidos."
        )
        String motivo
){
}
