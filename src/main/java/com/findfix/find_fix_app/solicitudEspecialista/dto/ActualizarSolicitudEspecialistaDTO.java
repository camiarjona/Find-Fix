package com.findfix.find_fix_app.solicitudEspecialista.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ActualizarSolicitudEspecialistaDTO (
        @NotNull(message = "El estado es obligatorio")
        @NotBlank(message = "El estado no puede quedar en blanco.")
        String estado,

        @NotNull (message = "La devolucion es obligatoria.")
        @NotBlank (message = "La devolucion no puede quedar en blanco")
        @Pattern(
                regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
                message = "No puede haber espacios al principio ni al final, ni multiples espacios seguidos."
        )
        String respuesta
){
        public boolean tieneEstado() {return estado != null;}
        public boolean tieneRespuesta() {return respuesta != null;}
}
